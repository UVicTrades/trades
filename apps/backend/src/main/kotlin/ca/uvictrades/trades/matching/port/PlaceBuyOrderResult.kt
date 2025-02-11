package ca.uvictrades.trades.matching.port

sealed class PlaceBuyOrderResult {
	data object Failure : PlaceBuyOrderResult()
	data class Success(
		val sellOrderResidues: List<SellLimitOrder>,
	) : PlaceBuyOrderResult()
}
