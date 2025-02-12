package ca.uvictrades.trades.persistence

import org.jooq.DSLContext
import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.StockTx.Companion.STOCK_TX
import ca.uvictrades.trades.model.public.tables.records.StockTxRecord
import ca.uvictrades.trades.model.public.tables.records.WalletTxRecord
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
}