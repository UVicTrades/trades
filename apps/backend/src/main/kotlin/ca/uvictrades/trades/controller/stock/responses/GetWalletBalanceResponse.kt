package ca.uvictrades.trades.controller.stock.responses

import java.math.BigDecimal

data class GetWalletBalanceResponse(
	val success: Boolean = true,
	val data: Data,
) {
	data class Data(
		val balance: BigDecimal,
	)
}
