package ca.uvictrades.trades.service

import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.persistence.StockRepository
import ca.uvictrades.trades.persistence.WalletRepository
import ca.uvictrades.trades.service.exceptions.StockCreationError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AdminService(
    private val stockRepository: StockRepository,
    private val walletRepository: WalletRepository,
) {
    @Transactional
    fun addMoneyToWallet(username: String, amount: BigDecimal) {
        walletRepository.addMoneyToWallet(username, amount)
        //Do not record a new transaction, this is just for adding funds to wallet initially
    }

    fun createStock(name: String): StockRecord {
        if (stockRepository.getStockWithName(name) != null) {
            throw StockCreationError("Stock with name: $name already exists")
        }

        val stockRecord = stockRepository.createStockWithName(name)

        return stockRecord

    }

    @Transactional
    fun addStockToUser(username: String, stockId: Int, quantity: Int) {
        stockRepository.addStockToUser(username, stockId, quantity)
    }

}