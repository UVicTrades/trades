package ca.uvictrades.trades.controller

import ca.uvictrades.trades.controller.responses.StockPriceResponse
import ca.uvictrades.trades.controller.responses.StockPricesResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/transaction")
class TransactionController {

    @GetMapping("/getStockPrices")
    fun getStockPrices(): StockPricesResponse {
        return StockPricesResponse(
            data = listOf(
                StockPriceResponse(
                    stock_id = 1,
                    stock_name = "Apple",
                    current_price = BigDecimal(100),
                ),
                StockPriceResponse(
                    stock_id = 2,
                    stock_name = "Google",
                    current_price = BigDecimal(200),
                ),
            )
        )
    }

}