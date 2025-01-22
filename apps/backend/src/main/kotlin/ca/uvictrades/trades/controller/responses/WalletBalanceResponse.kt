package ca.uvictrades.trades.controller.responses

import java.math.BigDecimal

data class WalletBalanceResponse (
    val success: Boolean = true,
    val data: WalletBalance
) {
    data class WalletBalance(
        val balance: BigDecimal,
    )
}
