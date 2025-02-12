package ca.uvictrades.trades.service

import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.persistence.StockRepository
import ca.uvictrades.trades.service.exceptions.StockCreationError
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val stockRepository: StockRepository,
) {

    fun createStock(name: String): StockRecord {
        if (stockRepository.getStockWithName(name) != null) {
            throw StockCreationError("Stock with name: $name already exists")
        }

        val stockRecord = stockRepository.createStockWithName(name)

        return stockRecord

    }

}