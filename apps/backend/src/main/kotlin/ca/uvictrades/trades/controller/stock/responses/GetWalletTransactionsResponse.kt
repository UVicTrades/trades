package ca.uvictrades.trades.controller.stock.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.OffsetDateTime

data class GetWalletTransactionsResponse(
	val success: Boolean = true,
	val data: List<WalletTransactionResponseElement>,
) {
	data class WalletTransactionResponseElement(
		val wallet_tx_id: String,
		val stock_tx_id: String,
		@field:JsonProperty("is_debit")
		val debit: Boolean,
		val amount: BigDecimal,
		val time_stamp: OffsetDateTime,
	)
}
