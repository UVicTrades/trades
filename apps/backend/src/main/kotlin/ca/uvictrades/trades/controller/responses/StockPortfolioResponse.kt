package ca.uvictrades.trades.controller.responses

data class StockPortfolioResponse(
    val success: Boolean = true,
    val data: List<StockPriceResponse>

)