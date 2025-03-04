package ca.uvictrades.trades.controller.stock

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.stock.responses.*
import ca.uvictrades.trades.persistence.SellOrderWithBuys
import ca.uvictrades.trades.persistence.StockRepository
import ca.uvictrades.trades.persistence.WalletRepository
import ca.uvictrades.trades.service.TradeService
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.ZoneOffset

@RestController
class StockController(
	private val walletRepo: WalletRepository,
	private val jwtVerifier: JwtVerifier,
	private val stockRepo: StockRepository,
	private val tradeService: TradeService,
) {

	@GetMapping("/transaction/getWalletBalance")
	fun getWalletBalance(
		@RequestHeader("token") token: String,
	): GetWalletBalanceResponse {
		val username = jwtVerifier.verify(token)

		val balance = walletRepo.getWalletBalance(username)

		return GetWalletBalanceResponse(
			data = GetWalletBalanceResponse.Data(
				balance = balance.toInt(),
			)
		)
	}

	@GetMapping("/transaction/getStockPortfolio")
	fun getStockPortfolio(
		@RequestHeader("token") token: String,
	): GetStockPortfolioResponse {
		val username = jwtVerifier.verify(token)
		val portfolio = stockRepo.getPortfolio(username)

		return GetStockPortfolioResponse(
			data = portfolio.map { it.toGetPortfolioDataElement() }
		)
	}

	@GetMapping("/transaction/getStockPrices")
	fun getStockPrices(
		@RequestHeader("token") token: String,
	): GetStockPricesResponse {
		return GetStockPricesResponse(
			data = tradeService
				.getStockPrices()
				.map {
					GetStockPricesResponse.StockPriceResponseElement(
						it.stockId.toString(),
						it.stockName,
						it.currentPrice.toInt(),
					)
				}
		)
	}

	@GetMapping("/transaction/getWalletTransactions")
	fun getWalletTransactions(
		@RequestHeader("token") token: String,
	): GetWalletTransactionsResponse {
		val username = jwtVerifier.verify(token)

		val responseElements = walletRepo.getWalletTransactionsAssociatedWithBuyOrder(username)
			.map {
				GetWalletTransactionsResponse.WalletTransactionResponseElement(
					it.id!!.toString(),
					it.buyOrderId!!.toString(),
					it.amount!! < BigDecimal.ZERO,
					it.amount!!.abs().toInt(),
					it.timeStamp!!.atOffset(ZoneOffset.UTC),
				)
			}
			.sortedBy { it.time_stamp }

		return GetWalletTransactionsResponse(
			data = responseElements,
		)
	}

	@GetMapping("/transaction/getStockTransactions")
	fun getStockTransactions(
		@RequestHeader("token") token: String,
	): GetStockTransactionsResponse {
//	): List<SellOrderWithBuys> {
		val username = jwtVerifier.verify(token)
		val responseElements = mutableListOf<GetStockTransactionsResponse.ResponseElement>()

		val sellOrders = tradeService.getSellOrdersWithAssociatedBuys(username)

		for (sellOrder in sellOrders) {
			val sumOfQuantitiesOfAssociatedBuyOrders = sellOrder.buyOrders.sumOf { it.quantity }

			if (
				sellOrder.buyOrders.count() > 1
				|| (
					sumOfQuantitiesOfAssociatedBuyOrders > BigInteger.ZERO
					&& sumOfQuantitiesOfAssociatedBuyOrders < sellOrder.quantity
				)
			) {
				// PARENT REGIME

				val order_status = if (sellOrder.isCancelled) {
					GetStockTransactionsResponse.OrderStatus.CANCELLED
				} else if (sumOfQuantitiesOfAssociatedBuyOrders < sellOrder.quantity) {
					GetStockTransactionsResponse.OrderStatus.PARTIALLY_COMPLETE
				} else {
					GetStockTransactionsResponse.OrderStatus.COMPLETED
				}

				val parentStockOrder = GetStockTransactionsResponse.ResponseElement(
					stock_tx_id = sellOrder.stockId.toString(),
					parent_stock_tx_id = null,
					stock_id = sellOrder.stockId.toString(),
					wallet_tx_id = null,
					order_status = order_status,
					buy = false,
					order_type = GetStockTransactionsResponse.OrderType.LIMIT,
					stock_price = sellOrder.pricePerShare.toInt(),
					quantity = sellOrder.quantity,
					time_stamp = sellOrder.timestamp.atOffset(ZoneOffset.UTC),
				)

				responseElements.add(parentStockOrder)

				// we need to process the buy orders as their own COMPLETE elements, too.
				sellOrder.buyOrders
					.map { buyOrder ->
						GetStockTransactionsResponse.ResponseElement(
							stock_tx_id = buyOrder.buyOrderId.toString(),
							parent_stock_tx_id = sellOrder.sellOrderId.toString(),
							stock_id = sellOrder.stockId.toString(),
							wallet_tx_id = buyOrder.sellerWalletTransactionId.toString(),
							order_status = GetStockTransactionsResponse.OrderStatus.COMPLETED,
							buy = false,
							order_type = GetStockTransactionsResponse.OrderType.LIMIT,
							stock_price = sellOrder.pricePerShare.toInt(),
							quantity = buyOrder.quantity,
							time_stamp = buyOrder.timestamp.atOffset(ZoneOffset.UTC),
						)
					}
					.forEach {
						responseElements.add(it)
					}

			} else {
				// STANDALONE REGIME

				if (sumOfQuantitiesOfAssociatedBuyOrders == BigInteger.ZERO) {
					// sell order is IN_PROGRESS
					val orderStatus = if (sellOrder.isCancelled) {
						GetStockTransactionsResponse.OrderStatus.CANCELLED
					} else {
						GetStockTransactionsResponse.OrderStatus.IN_PROGRESS
					}

					val parentOrderResponse = GetStockTransactionsResponse.ResponseElement(
						stock_tx_id = sellOrder.sellOrderId.toString(),
						parent_stock_tx_id = null,
						stock_id = sellOrder.stockId.toString(),
						wallet_tx_id = null,
						order_status = orderStatus,
						buy = false,
						order_type = GetStockTransactionsResponse.OrderType.LIMIT,
						stock_price = sellOrder.pricePerShare.toInt(),
						quantity = sellOrder.quantity,
						time_stamp = sellOrder.timestamp.atOffset(ZoneOffset.UTC),
					)

					responseElements.add(parentOrderResponse)
				} else {
					val orderStatus = if (sellOrder.isCancelled) {
						GetStockTransactionsResponse.OrderStatus.CANCELLED
					} else {
						GetStockTransactionsResponse.OrderStatus.COMPLETED
					}

					val parentOrderResponse = GetStockTransactionsResponse.ResponseElement(
						stock_tx_id = sellOrder.sellOrderId.toString(),
						parent_stock_tx_id = null,
						stock_id = sellOrder.stockId.toString(),
						wallet_tx_id = sellOrder.buyOrders.first().sellerWalletTransactionId.toString(),
						order_status = orderStatus,
						buy = false,
						order_type = GetStockTransactionsResponse.OrderType.LIMIT,
						stock_price = sellOrder.pricePerShare.toInt(),
						quantity = sellOrder.quantity,
						time_stamp = sellOrder.timestamp.atOffset(ZoneOffset.UTC),
					)

					responseElements.add(parentOrderResponse)
				}
			}

		}

		val buyOrders = tradeService.getBuyOrders(username)

		for (buyOrder in buyOrders) {
			val element = GetStockTransactionsResponse.ResponseElement(
				stock_tx_id = buyOrder.buyOrderId.toString(),
				parent_stock_tx_id = null,
				stock_id = buyOrder.stockId.toString(),
				wallet_tx_id = buyOrder.walletTransactionId.toString(),
				order_status = GetStockTransactionsResponse.OrderStatus.COMPLETED,
				buy = true,
				order_type = GetStockTransactionsResponse.OrderType.MARKET,
				stock_price = buyOrder.pricePerShare.toInt(),
				quantity = buyOrder.quantity,
				time_stamp = buyOrder.timestamp.atOffset(ZoneOffset.UTC),
			)

			responseElements.add(element)
		}

		val sortedResponseElements = responseElements.sortedBy { it.time_stamp }

		return GetStockTransactionsResponse(
			data = sortedResponseElements.sortedBy { it.time_stamp },
		)
	}

}
