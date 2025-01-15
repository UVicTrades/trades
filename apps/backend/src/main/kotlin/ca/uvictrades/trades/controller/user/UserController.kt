package ca.uvictrades.trades.controller.user

import ca.uvictrades.trades.configuration.JwtGenerator
import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.user.requests.LoginUserRequest
import ca.uvictrades.trades.controller.user.requests.RegisterUserRequest
import ca.uvictrades.trades.controller.user.responses.LoginUserResponse
import ca.uvictrades.trades.controller.user.responses.RegisterUserResponse
import ca.uvictrades.trades.model.public.tables.records.TraderRecord
import ca.uvictrades.trades.service.TraderService
import org.springframework.http.ResponseEntity
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
        traderService.registerNewTrader(
            request.user_name,
            request.password,
            request.name,
        )

        return RegisterUserResponse()
    }

    @PostMapping("/authentication/login")
    fun loginUser(@RequestBody request: LoginUserRequest): LoginUserResponse {
        val trader = traderService.loginTrader(request.user_name, request.password)

        val jwt = jwtGenerator.generate(trader.username!!)

        return LoginUserResponse(
            data = LoginUserResponse.TokenData(jwt)
        )
    }


}