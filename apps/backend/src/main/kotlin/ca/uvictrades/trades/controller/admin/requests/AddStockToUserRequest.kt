package ca.uvictrades.trades.controller.admin.requests

data class AddStockToUserRequest(
	val stock_id: String,
	val quantity: Int,
)
