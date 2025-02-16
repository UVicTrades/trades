package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.controller.responses.StockTransactionResponse
//import ca.uvictrades.trades.controller.trade.requests.PlaceStockOrderRequest
import ca.uvictrades.trades.model.public.enums.Ordertype
import ca.uvictrades.trades.model.public.enums.Status
import org.jooq.DSLContext
import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDINGS
import ca.uvictrades.trades.model.public.tables.StockOrder.Companion.STOCK_ORDER
import ca.uvictrades.trades.model.public.tables.records.StockOrderRecord
import ca.uvictrades.trades.model.public.tables.references.WALLET_TX
import org.jooq.Record
import org.jooq.Result


import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class StockTransactionDTO(
    val stockTxId: Int,
    val parentStockTxId: Int?,
    val stockId: Int,
    val walletTxId: Int?,
    val orderStatus: Status,
    val isBuy: Boolean,
    val orderType: Ordertype?,
    val stockPrice: BigDecimal,
    val quantity: Int,
    val timeStamp: LocalDateTime,
)

@Repository
class StockRepository (
    private val create: DSLContext,
) {

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

    fun getAllStockTransactionsByUserWithWalletTxId(username: String): List<StockTransactionDTO> {
        return create.select(
            STOCK_ORDER.STOCK_TX_ID,
            STOCK_ORDER.PARENT_STOCK_TX_ID,
            STOCK_ORDER.STOCK_ID,
            WALLET_TX.WALLET_TX_ID,
            STOCK_ORDER.ORDER_STATUS,
            STOCK_ORDER.IS_BUY,
            STOCK_ORDER.ORDER_TYPE,
            STOCK_ORDER.STOCK_PRICE,
            STOCK_ORDER.TIME_STAMP
        )
            .from(STOCK_ORDER)
            .leftJoin(WALLET_TX).on(STOCK_ORDER.STOCK_TX_ID.eq(WALLET_TX.STOCK_TX_ID))
            .where(STOCK_ORDER.OWNER.eq(username))
            .fetchInto(StockTransactionDTO::class.java)
    }
}
