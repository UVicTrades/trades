package ca.uvictrades.trades.controller.user.requests

data class RegisterUserRequest(
    val user_name: String,
    val password: String,
    val name: String,
)
