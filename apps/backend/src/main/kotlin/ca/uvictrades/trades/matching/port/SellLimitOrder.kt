package ca.uvictrades.trades.matching.port

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

data class SellLimitOrder(
	val id: String,
	val stock: String,
	val quantity: BigInteger,
	val pricePerUnit: BigDecimal,
	val timestamp: Instant,
	val trader: String
)
