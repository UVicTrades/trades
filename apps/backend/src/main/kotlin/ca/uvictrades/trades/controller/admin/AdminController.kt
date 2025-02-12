package ca.uvictrades.trades.controller.admin

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.service.AdminService
import ca.uvictrades.trades.controller.admin.requests.CreateStockRequest
import ca.uvictrades.trades.controller.admin.responses.CreateStockResponse
import ca.uvictrades.trades.controller.responses.WalletTransactionsResponse
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController


@RestController
class AdminController(
    private val jwtVerifier: JwtVerifier,
    private val adminService: AdminService,
) {

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
                    stock_id = result.stockName!!
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

}