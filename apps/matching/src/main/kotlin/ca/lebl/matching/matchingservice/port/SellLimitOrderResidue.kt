package ca.lebl.matching.matchingservice.port

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

data class SellLimitOrderResidue(
	val orderId: String,
	val stockId: String,
	val matchedQuantity: BigInteger,
	val remainingQuantity: BigInteger,
	val pricePerUnit: BigDecimal,
	val timestamp: Instant,
	val trader: String
)
