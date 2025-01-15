package ca.uvictrades.trades.controller.responses

data class StockPricesResponse(
    val success: Boolean = true,
    val data: List<StockPriceResponse>
)
