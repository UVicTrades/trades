package ca.uvictrades.trades.matching.port

import java.math.BigDecimal
import java.time.Instant

data class SellLimitOrderResidue(
	val orderId: String,
	val stockId: String,
	val matchedQuantity: Int,
	val remainingQuantity: Int,
	val pricePerUnit: BigDecimal,
	val timestamp: Instant,
)
