package ca.uvictrades.trades.matching.port

import java.math.BigDecimal

sealed class PlaceBuyOrderResult {
	data object Failure : PlaceBuyOrderResult()
	data class Success(
		val sellOrderResidues: List<SellLimitOrderResidue>,
		val residualLiquidity: BigDecimal,
	) : PlaceBuyOrderResult()
}
