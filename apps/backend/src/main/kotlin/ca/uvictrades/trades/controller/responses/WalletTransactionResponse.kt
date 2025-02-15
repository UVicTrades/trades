package ca.uvictrades.trades.controller.responses

import ca.uvictrades.trades.model.public.tables.records.WalletTxRecord
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset


data class WalletTransactionResponse(
    val wallet_tx_id: String,
    val stock_tx_id: String,
    @get:JsonProperty("is_debit")
    val is_debit: Boolean,
    val amount: BigDecimal,
    val time_stamp: OffsetDateTime, //Use atOffset(timezone.UTC) when assigning this type
)

fun WalletTxRecord.toWalletTransactionResponse(): WalletTransactionResponse {

    // TODO: nullability fuckery
    return WalletTransactionResponse(
        wallet_tx_id = walletTxId?.toString()!!,
        stock_tx_id = stockTxId?.toString()!!,
        is_debit = isDebit!!,
        amount = amount!!,
        time_stamp = timeStamp?.atOffset(ZoneOffset.UTC)!!,
    )
}