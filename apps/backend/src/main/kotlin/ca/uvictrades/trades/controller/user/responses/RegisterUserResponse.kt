package ca.uvictrades.trades.controller.user.responses

import ca.uvictrades.trades.controller.responses.ErrorResponse

data class RegisterUserResponse(
    val success: Boolean,
	val data: ErrorResponse? = null
)
