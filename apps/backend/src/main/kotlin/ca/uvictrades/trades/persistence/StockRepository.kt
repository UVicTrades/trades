package ca.uvictrades.trades.persistence

import org.jooq.DSLContext
import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import org.springframework.stereotype.Repository

@Repository
class StockRepository (
    private val create: DSLContext,
) {

    fun getAllStockPrices(): List<StockRecord> {
        return create.selectFrom(STOCK)
            .fetch()
            .map{ it.into(StockRecord::class.java)}
    }

    fun getStockWithName(name: String): StockRecord? {
        return create.selectFrom(STOCK)
            .where(STOCK.STOCK_NAME.eq(name))
            .fetchOne()
    }

    fun createStockWithName(name: String): StockRecord {
        val record = create.newRecord(STOCK).apply {
            stockName = name
        }

        record.store()

        return record
    }
}