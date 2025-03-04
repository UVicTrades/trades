package ca.uvictrades.trades.controller.admin

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.admin.requests.AddMoneyToWalletRequest
import ca.uvictrades.trades.controller.admin.requests.AddStockToUserRequest
import ca.uvictrades.trades.controller.admin.requests.CreateStockRequest
import ca.uvictrades.trades.controller.admin.responses.CreateStockResponse
import ca.uvictrades.trades.controller.shared.SuccessTrueDataNull
import ca.uvictrades.trades.persistence.StockRepository
import ca.uvictrades.trades.persistence.WalletRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController(
	private val jwtVerifier: JwtVerifier,
	private val walletRepo: WalletRepository,
	private val stockRepo: StockRepository,
) {

	@PostMapping("/transaction/addMoneyToWallet")
	fun addMoneyToWallet(
		@RequestHeader("token") token: String,
		@RequestBody body: AddMoneyToWalletRequest,
	): SuccessTrueDataNull {

		val username = jwtVerifier.verify(token)

		walletRepo.creditOrDebitTrader(username, body.amount)

		return SuccessTrueDataNull()

	}

	@PostMapping("/setup/createStock")
	fun createStock(
		@RequestHeader("token") token: String,
		@RequestBody body: CreateStockRequest,
	): CreateStockResponse {

		jwtVerifier.verify(token)

		val record = stockRepo.createStock(body.stock_name)

		return CreateStockResponse(
			data = CreateStockResponse.Data(
				stock_id = record.id.toString(),
			)
		)

		// TODO: when stock name collides, return error
	}

	@PostMapping("/setup/addStockToUser")
	fun addStockToUser(
		@RequestHeader("token") token: String,
		@RequestBody body: AddStockToUserRequest,
	): SuccessTrueDataNull {
		val username = jwtVerifier.verify(token)

		stockRepo.addStockToUser(username, body.stock_id.toInt(), body.quantity)
		return SuccessTrueDataNull()
	}

}
