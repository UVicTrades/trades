package ca.uvictrades.trades.controller.user.requests

data class LoginUserRequest(
    val user_name: String,
    val password: String,
)
