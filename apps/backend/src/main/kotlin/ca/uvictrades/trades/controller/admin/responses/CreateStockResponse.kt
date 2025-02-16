package ca.uvictrades.trades.controller.admin.responses

data class CreateStockResponse(
	val success: Boolean = true,
	val data: Data,
) {
	data class Data(
		val stock_id: String,
	)
}
