package ca.uvictrades.trades.controller.shared

data class SuccessTrueDataNull(
	val success: Boolean = true,
	val data: Any? = null,
) {
	data class Error(
		val error: String
	)
}
