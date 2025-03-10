package ca.lebl.matching.matchingservice.port

import java.math.BigDecimal
import java.math.BigInteger

data class BuyMarketOrder(
	val stock: String,
	val quantity: BigInteger,
	val liquidity: BigDecimal,
	val trader: String
)
