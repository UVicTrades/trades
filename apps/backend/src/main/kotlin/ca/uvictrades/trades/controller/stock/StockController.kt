package ca.uvictrades.trades.controller.stock

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.stock.responses.*
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
		try {
			val username = jwtVerifier.verify(token)

			val balance = walletRepo.getWalletBalance(username)

			return GetWalletBalanceResponse(
				data = GetWalletBalanceResponse.Data(
					balance = balance,
				)
			)

		} catch (e: JwtException) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
		}
	}

	@GetMapping("/transaction/getStockPortfolio")
	fun getStockPortfolio(
		@RequestHeader("token") token: String,
	): GetStockPortfolioResponse {
		try {
			val username = jwtVerifier.verify(token)
			val portfolio = stockRepo.getPortfolio(username)

			return GetStockPortfolioResponse(
				data = portfolio.map { it.toGetPortfolioDataElement() }
			)
		} catch (e: JwtException) {
			TODO("Not yet implemented")
		}
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
						it.currentPrice,
					)
				}
		)
	}

	@GetMapping("/transaction/getWalletTransactions")
	fun getWalletTransactions(
		@RequestHeader("token") token: String,
	): GetWalletTransactionsResponse {
		try {
		    val username = jwtVerifier.verify(token)

			val responseElements = walletRepo.getWalletTransactionsAssociatedWithBuyOrder(username)
				.map {
					GetWalletTransactionsResponse.WalletTransactionResponseElement(
						it.id!!.toString(),
						it.buyOrderId!!.toString(),
						it.amount!! < BigDecimal.ZERO,
						it.amount!!.abs(),
						Instant.now().atOffset(ZoneOffset.UTC),
					)
				}

			return GetWalletTransactionsResponse(
				data = responseElements,
			)

		} catch (e: JwtException) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
		}
	}


}
