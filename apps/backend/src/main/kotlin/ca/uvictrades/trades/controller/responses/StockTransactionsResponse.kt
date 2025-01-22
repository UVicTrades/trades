package ca.uvictrades.trades.controller.responses

data class StockTransactionsResponse(
    val success: Boolean = true,
    val data: List<StockTransactionResponse>
)
