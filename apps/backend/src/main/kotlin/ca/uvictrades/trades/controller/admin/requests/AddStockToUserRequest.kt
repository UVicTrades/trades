package ca.uvictrades.trades.controller.admin.requests

import java.math.BigInteger

data class AddStockToUserRequest(
	val stock_id: String,
	val quantity: BigInteger,
)
