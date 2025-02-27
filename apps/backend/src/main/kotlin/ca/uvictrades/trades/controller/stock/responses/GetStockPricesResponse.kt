package ca.uvictrades.trades.controller.stock.responses

import java.math.BigDecimal

data class GetStockPricesResponse(
	val success: Boolean = true,
	val data: List<StockPriceResponseElement>,
) {
	data class StockPriceResponseElement(
		val stock_id: String,
		val stock_name: String,
		val current_price: Int,
	)
}
