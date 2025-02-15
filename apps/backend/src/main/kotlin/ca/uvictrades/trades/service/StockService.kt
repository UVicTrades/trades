package ca.uvictrades.trades.service

import ca.uvictrades.trades.controller.responses.StockPriceResponse
import ca.uvictrades.trades.controller.responses.StockTransactionResponse
import ca.uvictrades.trades.controller.responses.toStockTransactionResponse
import ca.uvictrades.trades.persistence.StockRepository
import org.springframework.stereotype.Component

@Component
class StockService(
    private val stockRepo: StockRepository,
) {
    fun getStockPrices(): List<StockPriceResponse> {
        return stockRepo.getAllStockPrices()
            .map { it.into(StockPriceResponse::class.java)}
    }

    fun getStockTransactions(username: String): List<StockTransactionResponse> {

        return stockRepo.getAllStockTransactionsByUserWithWalletTxId(username)
            .map{ it.toStockTransactionResponse()}
    }
}