package ca.uvictrades.trades.controller.responses

import java.math.BigDecimal
import java.time.OffsetDateTime

data class WalletTransactionResponse(
    val wallet_tx_id: String,
    val stock_tx_id: String,
    val is_debit: Boolean,
    val amount: BigDecimal,
    val time_stamp: OffsetDateTime, //Use atOffset(timezone.UTC) when assigning this type
)