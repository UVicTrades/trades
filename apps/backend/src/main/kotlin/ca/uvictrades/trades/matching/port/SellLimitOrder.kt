package ca.uvictrades.trades.matching.port

import java.math.BigDecimal

data class SellLimitOrder(
	val id: String,
	val stock: String,
	val quantity: Int,
	val pricePerUnit: BigDecimal,
)
