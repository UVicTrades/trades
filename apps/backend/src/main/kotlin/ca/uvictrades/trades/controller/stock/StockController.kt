package ca.uvictrades.trades.controller.stock

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.stock.responses.GetWalletBalanceResponse
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

}
