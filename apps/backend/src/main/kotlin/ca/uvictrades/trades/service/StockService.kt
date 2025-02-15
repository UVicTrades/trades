package ca.uvictrades.trades.service

import ca.uvictrades.trades.controller.responses.StockPriceResponse
import ca.uvictrades.trades.controller.responses.StockTransactionResponse
import ca.uvictrades.trades.controller.responses.toStockTransactionResponse
import ca.uvictrades.trades.persistence.StockRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StockService(
    private val stockRepo: StockRepository,
) {
    fun getStockPrices(): List<StockPriceResponse> {
        //TODO: CALL MATCHING ENGINE
        return listOf(
            StockPriceResponse(
                stock_id = "1",
                stock_name = "Apple",
                current_price = BigDecimal(100),
            ),
            StockPriceResponse(
                stock_id = "2",
                stock_name = "Google",
                current_price = BigDecimal(200),
            )
        )
    }

    fun getStockTransactions(username: String): List<StockTransactionResponse> {

        return stockRepo.getAllStockTransactionsByUserWithWalletTxId(username)
            .map{ it.toStockTransactionResponse()}
    }
}