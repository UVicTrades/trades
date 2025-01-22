package ca.uvictrades.trades.controller.responses

import java.math.BigDecimal

data class StockPriceResponse(
    val stock_id: String,
    val stock_name: String,
    val current_price: BigDecimal,
)
