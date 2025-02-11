package ca.uvictrades.trades.matching.port

import java.math.BigDecimal
import java.time.Instant

data class SellLimitOrder(
	val id: String,
	val stock: String,
	val quantity: Int,
	val pricePerUnit: BigDecimal,
	val timestamp: Instant,
)
