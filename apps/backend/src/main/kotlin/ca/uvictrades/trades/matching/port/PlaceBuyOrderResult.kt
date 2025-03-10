package ca.uvictrades.trades.matching.port

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "type"
)
@JsonSubTypes(
	JsonSubTypes.Type(value = PlaceBuyOrderResult.Success::class, name = "success"),
	JsonSubTypes.Type(value = PlaceBuyOrderResult.Failure::class, name = "failure")
)
sealed class PlaceBuyOrderResult {
	data object Failure : PlaceBuyOrderResult()
	data class Success(
		val sellOrderResidues: List<SellLimitOrderResidue>,
		val residualLiquidity: BigDecimal,
	) : PlaceBuyOrderResult()
}
