package ca.uvictrades.trades.controller.user.responses

import ca.uvictrades.trades.controller.responses.ErrorResponse

data class LoginUserResponse(
	val success: Boolean,
	val data: Any?
) {
	data class TokenData(
		val token: String
	)
}
