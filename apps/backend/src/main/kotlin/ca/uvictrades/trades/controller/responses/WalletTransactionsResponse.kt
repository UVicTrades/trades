package ca.uvictrades.trades.controller.responses

data class WalletTransactionsResponse(
    val success: Boolean = true,
    val data : List<WalletTransactionResponse>
)