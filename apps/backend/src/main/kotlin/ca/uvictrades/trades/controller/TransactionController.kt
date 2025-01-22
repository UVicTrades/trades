package ca.uvictrades.trades.controller

import ca.uvictrades.trades.controller.responses.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.OffsetDateTime

@RestController
@RequestMapping("/transaction")
class TransactionController {

    @GetMapping("/getStockPrices")
    fun getStockPrices(): StockPricesResponse {
        return StockPricesResponse(
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

    @GetMapping("/getStockPortfolio")
    fun getStockPortfolio(): StockPortfolioResponse {
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
    fun getWalletBalance(): WalletBalanceResponse {
        return WalletBalanceResponse(
            data = WalletBalanceResponse.WalletBalance(BigDecimal(100))
        )
    }

    @GetMapping("/getWalletTransactions")
    fun getWalletTransactions(): WalletTransactionsResponse {
        return WalletTransactionsResponse(
            data = listOf(
                WalletTransactionResponse(
                    wallet_tx_id = "628ba23df2210df6c3764823",
                    stock_tx_id = "62738363a50350b1fbb243a6",
                    is_debit = true,
                    amount = BigDecimal(100),
                    time_stamp = OffsetDateTime.parse("2024-01-12T15:03:25.019+00:00"),
                ),
            )
        )
    }

    @GetMapping("/getStockTransactions")
    fun getStockTransactions(): StockTransactionsResponse {
        return StockTransactionsResponse(
            data = listOf(
                StockTransactionResponse(
                    stock_tx_id = "62738363a50350b1fbb243a6",
                    stock_id = "1",
                    wallet_tx_id = "628ba23df2210df6c3764823",
                    order_status = StockTransactionResponse.OrderStatus.COMPLETED,
                    is_buy = true,
                    order_type = StockTransactionResponse.OrderType.LIMIT,
                    stock_price = BigDecimal(50),
                    quantity = 2,
                    time_stamp = OffsetDateTime.parse("2024-01-12T15:03:25.019+00:00")
                ),
                StockTransactionResponse(
                    stock_tx_id = "62738363a50350b1fbb243a6",
                    stock_id = "1",
                    wallet_tx_id = "628ba23df2210df6c3764823",
                    order_status = StockTransactionResponse.OrderStatus.COMPLETED,
                    is_buy = false,
                    order_type = StockTransactionResponse.OrderType.MARKET,
                    stock_price = BigDecimal(50),
                    quantity = 2,
                    time_stamp = OffsetDateTime.parse("2024-01-12T15:03:25.019+00:00")
                )
            )
        )
    }

}