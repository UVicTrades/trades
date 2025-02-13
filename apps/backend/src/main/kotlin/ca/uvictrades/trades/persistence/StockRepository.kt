package ca.uvictrades.trades.persistence

import org.jooq.DSLContext
import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDINGS
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

    fun addStockToUser(username: String, stockId: Int, quantity: Int) {
        with(STOCK_HOLDINGS) {
            create.insertInto(STOCK_HOLDINGS)
                    .set(STOCK_ID, stockId)
                    .set(TRADER_USERNAME, username)
                    .set(QUANTITY, quantity)
                .onConflict(STOCK_ID, TRADER_USERNAME)
                .doUpdate()
                .set(QUANTITY, QUANTITY.plus(quantity))
                .execute()
        }
    }
}