package ca.uvictrades.trades.controller.stock

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.stock.responses.GetStockPortfolioResponse
import ca.uvictrades.trades.controller.stock.responses.toGetStockPricesDataElement
import ca.uvictrades.trades.controller.stock.responses.GetStockPricesResponse
import ca.uvictrades.trades.controller.stock.responses.GetWalletBalanceResponse
import ca.uvictrades.trades.controller.stock.responses.toGetPortfolioDataElement
import ca.uvictrades.trades.persistence.StockRepository
import ca.uvictrades.trades.persistence.WalletRepository
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class StockController(
	private val walletRepo: WalletRepository,
	private val jwtVerifier: JwtVerifier,
	private val stockRepo: StockRepository,
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
		try {
			jwtVerifier.verify(token)
			val prices = stockRepo.getSellOrderPrices()

			return GetStockPricesResponse(
				data = prices.map { it.toGetStockPricesDataElement() }
			)
		} catch (e: JwtException) {
			throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
		}
	}

}
