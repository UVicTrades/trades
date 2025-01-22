package ca.uvictrades.trades.controller.responses

import java.math.BigDecimal
import java.time.OffsetDateTime

data class StockTransactionResponse (
    val stock_tx_id: String,
    val stock_id: String,
    val wallet_tx_id: String,
    val order_status: OrderStatus,
    val is_buy: Boolean,
    val order_type: OrderType,
    val stock_price: BigDecimal,
    val quantity: Int,
    val time_stamp: OffsetDateTime,

    ) {
    enum class OrderStatus {
        COMPLETED, CANCELLED, IN_PROGRESS, PARTIALLY_COMPLETE
    }
    enum class OrderType {
        MARKET, LIMIT
    }
}