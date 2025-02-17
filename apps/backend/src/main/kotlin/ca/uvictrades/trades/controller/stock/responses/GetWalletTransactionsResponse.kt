package ca.uvictrades.trades.controller.stock.responses


import ca.uvictrades.trades.model.public.tables.records.WalletTransactionRecord
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class GetWalletTransactionsResponse(
	val success: Boolean = true,
	val data: List<DataElement>,
) {
	data class DataElement(
		val wallet_tx_id: String,
		val stock_tx_id: String,
		val is_debit: Boolean,
		val amount: BigDecimal,
		val time_stamp: OffsetDateTime
	)
}

fun WalletTransactionRecord.toGetWalletTransactionDataElement(): GetWalletTransactionsResponse.DataElement {
	return GetWalletTransactionsResponse.DataElement(
		wallet_tx_id = this.id.toString(),
		stock_tx_id = this.buyOrderId.toString(),
		amount = this.amount!!,
		is_debit = this.amount!! > BigDecimal.ZERO,
		time_stamp = this.timeStamp!!.atOffset(ZoneOffset.UTC)
	)
}
