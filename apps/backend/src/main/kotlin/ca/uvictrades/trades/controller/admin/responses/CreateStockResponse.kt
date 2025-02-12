package ca.uvictrades.trades.controller.admin.responses

data class CreateStockResponse(
    val success: Boolean = true,
    val data: StockIdData?,
) {
    data class StockIdData(
        val stock_id: String
    )
}
