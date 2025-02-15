package ca.uvictrades.trades.controller


import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.responses.*
import ca.uvictrades.trades.service.StockService
import ca.uvictrades.trades.service.WalletService
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/transaction")
class TransactionController (
    private val walletService: WalletService,
    private val jwtVerifier: JwtVerifier,
    private val stockService: StockService,
) {

    @GetMapping("/getStockPrices")
    fun getStockPrices(@RequestHeader("token") authHeader: String): ResponseEntity<StockPricesResponse> {
        val dataPayload: List<StockPriceResponse>?

        try{
            jwtVerifier.verify(authHeader)

            dataPayload = stockService.getStockPrices()

            return ResponseEntity.ok(StockPricesResponse(
                success = true,
                data = dataPayload
            ))


        } catch(e: JwtException) {
            // if we're in this block, it means the user's JWT was invalid
            // thus, we return a 401.
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(StockPricesResponse(
                    success = false,
                    data = null
                ))
        }
    }

    @GetMapping("/getStockPortfolio")
    fun getStockPortfolio(@RequestHeader("token") authHeader: String): StockPortfolioResponse {
        return StockPortfolioResponse(
            data = listOf(
                StockPriceResponse(
                    stock_id = "1",
                    stock_name = "Apple",
                    current_price = BigDecimal(100),
                ),
                StockPriceResponse(
                    stock_id = "2",
                    stock_name = "Google",
                    current_price = BigDecimal(200),
                ),
            )
        )
    }

    @GetMapping("/getWalletBalance")
    fun getWalletBalance(@RequestHeader("token") authHeader: String): ResponseEntity<WalletBalanceResponse> {
        //Note: Bruno currently passes token as auth bearer token while project spec specifies passing as a header value with name=token
        var dataPayload: WalletBalanceResponse.WalletBalance?

        try {
            val username = jwtVerifier.verify(authHeader)

            val balance = walletService.getWalletBalanceByUserName(username)?.balance
            dataPayload = WalletBalanceResponse.WalletBalance(balance)

            return ResponseEntity.ok(WalletBalanceResponse(
                success = true,
                data = dataPayload
            ))

        } catch(e: JwtException) {
            // if we're in this block, it means the user's JWT was invalid
            // thus, we return a 401.
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(WalletBalanceResponse(
                    success = false,
                    data = null
                ))
        }
    }

    @GetMapping("/getWalletTransactions")
    fun getWalletTransactions(@RequestHeader("token") authHeader: String): ResponseEntity<WalletTransactionsResponse> {
        var walletTransactions: List<WalletTransactionResponse>?

        try {
            val username = jwtVerifier.verify(authHeader)

            walletTransactions = walletService.getWalletTransactions(username)

            return ResponseEntity.ok(WalletTransactionsResponse(
                success = true,
                data = walletTransactions
            ))
        } catch (e: JwtException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(WalletTransactionsResponse(
                    success = false,
                    data = null
                ))
        }

    }

    @GetMapping("/getStockTransactions")
    fun getStockTransactions(@RequestHeader("token") authHeader: String): ResponseEntity<StockTransactionsResponse> {
        try {
            val username = jwtVerifier.verify(authHeader)

            val dataPayload = stockService.getStockTransactions(username)

            return ResponseEntity.ok(StockTransactionsResponse(
                success = true,
                data = dataPayload
            ))
        } catch (e: JwtException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(StockTransactionsResponse(
                    success = false,
                    data = null
                ))
        }
    }

}
