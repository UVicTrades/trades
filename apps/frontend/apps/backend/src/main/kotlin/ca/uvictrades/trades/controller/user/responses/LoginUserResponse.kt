package ca.uvictrades.trades.controller.user.responses

data class LoginUserResponse(
    val success: Boolean = true,
    val data: TokenData,
) {
    data class TokenData(
        val token: String,
    )
}
