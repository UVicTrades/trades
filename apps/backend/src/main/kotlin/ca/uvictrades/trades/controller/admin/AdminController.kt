package ca.uvictrades.trades.controller.admin

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.admin.requests.AddMoneyToWalletRequest

import ca.uvictrades.trades.service.AdminService
import ca.uvictrades.trades.controller.admin.requests.CreateStockRequest
import ca.uvictrades.trades.controller.admin.requests.AddStockToUserRequest
import ca.uvictrades.trades.controller.admin.responses.CreateStockResponse
import ca.uvictrades.trades.controller.shared.SuccessTrueDataNull
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.Unauthorized
import org.springframework.web.server.ResponseStatusException


@RestController
class AdminController(
    private val jwtVerifier: JwtVerifier,
    private val adminService: AdminService,
) {

    @PostMapping("/transaction/addMoneyToWallet")
    fun addMoneyToWallet(
        @RequestHeader("token") authHeader: String,
        @RequestBody request: AddMoneyToWalletRequest
    ): ResponseEntity<SuccessTrueDataNull> {
        try {
            val username = jwtVerifier.verify(authHeader)

            adminService.addMoneyToWallet(username, request.amount)

            return ResponseEntity.ok(SuccessTrueDataNull(
                success = true,
                data = null,
            ))
        } catch (e: JwtException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    SuccessTrueDataNull(
                        success = false,
                        data = null
                    )
                )
        }
    }


    @PostMapping("/setup/createStock")
    fun createStock(
        @RequestHeader("token") authHeader: String,
        @RequestBody request: CreateStockRequest
    ): ResponseEntity<CreateStockResponse> {
        try {
            val username = jwtVerifier.verify(authHeader)

            val result = adminService.createStock(request.stock_name)

            return ResponseEntity.ok(CreateStockResponse(
                success = true,
                data = CreateStockResponse.StockIdData(
                    stock_id = result.stockId!!.toString()
                )
            ))
        } catch (e: JwtException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    CreateStockResponse(
                    success = false,
                    data = null
                )
            )
        }
    }

    @PostMapping("/setup/addStockToUser")
    fun addStockToUser(
        @RequestHeader("token") token: String,
        @RequestBody request: AddStockToUserRequest
    ): SuccessTrueDataNull {
        try {
            val username = jwtVerifier.verify(token)

            adminService.addStockToUser(username, request.stock_id.toInt(), request.quantity)
        } catch (e: JwtException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        return SuccessTrueDataNull()
    }

}
