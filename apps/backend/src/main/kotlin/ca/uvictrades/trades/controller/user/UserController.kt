package ca.uvictrades.trades.controller.user

import ca.uvictrades.trades.configuration.JwtGenerator
import ca.uvictrades.trades.controller.responses.ErrorResponse
import ca.uvictrades.trades.controller.user.requests.LoginUserRequest
import ca.uvictrades.trades.controller.user.requests.RegisterUserRequest
import ca.uvictrades.trades.controller.user.responses.LoginUserResponse
import ca.uvictrades.trades.controller.user.responses.RegisterUserResponse
import ca.uvictrades.trades.service.TraderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val traderService: TraderService,
    private val jwtGenerator: JwtGenerator,
) {

    @PostMapping("/authentication/register")
    fun registerUser(@RequestBody request: RegisterUserRequest): RegisterUserResponse {
		return try {
			traderService.registerNewTrader(
				request.user_name,
				request.password,
				request.name,
			)
			RegisterUserResponse(success = true)
		} catch(e: Exception){
			RegisterUserResponse(success = false, data = ErrorResponse(error = "Registration failed"))
		}

    }

    @PostMapping("/authentication/login")
    fun loginUser(@RequestBody request: LoginUserRequest): LoginUserResponse {
		return try {
			val trader = traderService.loginTrader(request.user_name, request.password)

			val jwt = jwtGenerator.generate(trader.username!!)
			LoginUserResponse(success = true, data = LoginUserResponse.TokenData(jwt))
		} catch (e: Exception) {
			LoginUserResponse(success = false, data = ErrorResponse(error = e.message ?: "Invalid credentials"))
		}
	}
}
