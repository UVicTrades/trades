package ca.uvictrades.trades.matching.port

import java.math.BigDecimal
import java.math.BigInteger

data class BuyMarketOrder(
	val stock: String,
	val quantity: BigInteger,
	val liquidity: BigDecimal,
	val trader: String
)
