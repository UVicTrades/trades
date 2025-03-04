package ca.uvictrades.trades.service

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.MatchingService
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import ca.uvictrades.trades.persistence.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

@Component
class TradeService(
	private val walletRepo: WalletRepository,
	private val matchingService: MatchingService,
	private val tradeRepo: TradeRepository,
	private val stockRepo: StockRepository,
) {

	@Transactional
	fun placeBuyOrder(
		trader: String,
		stockId: Int,
		quantity: BigInteger,
	) {
		val liquidity = walletRepo.getWalletBalance(trader)

		val order = BuyMarketOrder(
			stockId.toString(),
			quantity,
			liquidity,
			trader,
		)

		val result = matchingService.place(order)

		if (result is PlaceBuyOrderResult.Failure) {
			error("Buy order failed")
		}

		require(result is PlaceBuyOrderResult.Success)

		val buyOrder = tradeRepo.newBuyOrder(
			trader,
			result.sellOrderResidues.map {
				SellOrderQuantity(
					it.orderId.toInt(),
					it.matchedQuantity
				)
			}
		)

		stockRepo.addStockToUser(
			trader,
			stockId,
			quantity,
		)

		walletRepo.addWalletTransaction(
			trader,
			result.residualLiquidity - liquidity,
			buyOrder.id!!,
		)

		val sellOrders = stockRepo.getSellOrders(
			result.sellOrderResidues.map { it.orderId.toInt() }
		)

//		val tradersMap = stockRepo.getTradersForSellOrders(
//			result.sellOrderResidues.map { it.orderId.toInt() }
//		).associate {
//			it.sellOrderId to it.trader
//		}

		result.sellOrderResidues.forEach { orderResidue ->
			val order = sellOrders.find { it.id!! == orderResidue.orderId.toInt() }!!
			walletRepo.addWalletTransaction(
				order.trader!!,
				BigDecimal(orderResidue.matchedQuantity) * order.pricePerShare!!,
				buyOrder.id!!,
			)
		}

	}

	fun placeSellOrder(username: String, stockId: Int, quantity: BigInteger, pricePerShare: BigDecimal) {
		val availableShares = stockRepo.getPortfolio(username)
			.find { it.stockId == stockId }
			?.quantity ?: BigInteger.ZERO

		if (availableShares < quantity) {
			error("Not enough stock available in portfolio to make this sell order")
		}

		stockRepo.subtractStockFromUser(
			username,
			stockId,
			quantity,
		)

		val sellOrderRecord = tradeRepo.newSellOrder(
			username,
			stockId,
			quantity,
			pricePerShare,
		)

		val sellOrder = SellLimitOrder(
            sellOrderRecord.id!!.toString(),
			stockId.toString(),
			quantity,
			pricePerShare,
			Instant.now(),
			username,
		)

		matchingService.place(sellOrder)
	}

	fun getStockPrices(): List<StockPrice> {
		val pricesFromMatching = matchingService.getStockPrices()

		val stockRecords = stockRepo.getStocksForIds(pricesFromMatching.keys.map { it.toInt() }.toSet())

		return pricesFromMatching.map { (stockId, price) ->
			StockPrice(
				stockId = stockId.toInt(),
				stockName = stockRecords.find { it.id!! == stockId.toInt() }!!.name!!,
				currentPrice = price,
			)
		}
	}

	fun cancelOrder(orderId: Int, traderUsername: String) {
		val order = tradeRepo.getSellOrder(orderId)

		if (order.trader != traderUsername) {
			error("Trader is unauthorized")
		}

		matchingService.cancelOrder(orderId.toString(), order.stockId.toString())

		tradeRepo.cancelSellOrder(orderId)
	}

	fun getSellOrdersWithAssociatedBuys(username: String): List<SellOrderWithBuys> =
		tradeRepo.getSellOrdersWithAssociatedBuys(username)

	fun getBuyOrders(forTrader: String): List<BuyOrderWithStockIdAndWalletTxId> {
		return tradeRepo.getBuyOrders(forTrader)
	}

}

data class StockPrice(
	val stockId: Int,
	val stockName: String,
	val currentPrice: BigDecimal,
)
