package ca.uvictrades.trades.matching.port

import java.math.BigDecimal

data class BuyMarketOrder(
	val stock: String,
	val quantity: Int,
	val liquidity: BigDecimal
)
