package ca.uvictrades.trades.controller.responses

import ca.uvictrades.trades.model.public.enums.Ordertype
import ca.uvictrades.trades.model.public.enums.Status
import ca.uvictrades.trades.model.public.tables.records.StockOrderRecord
import ca.uvictrades.trades.persistence.StockTransactionDTO
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class StockTransactionResponse(
    val stock_tx_id: String,
    val parent_stock_tx_id: String?,
    val stock_id: String,
    val wallet_tx_id: String?,
    val order_status: Status,
    val is_buy: Boolean,
    val order_type: Ordertype?,
    val stock_price: BigDecimal,
    val quantity: Int,
    val time_stamp: OffsetDateTime,
    )

fun StockTransactionDTO.toStockTransactionResponse(): StockTransactionResponse {

    return StockTransactionResponse(
        stock_tx_id = this.stockTxId.toString(),
        parent_stock_tx_id = this.parentStockTxId?.toString(),
        stock_id = this.stockId.toString(),
        wallet_tx_id = this.walletTxId?.toString(),
        order_status = this.orderStatus,
        is_buy = this.isBuy,
        order_type = this.orderType,
        stock_price = this.stockPrice,
        quantity = this.quantity,
        time_stamp = timeStamp.atOffset(ZoneOffset.UTC)!!
    )
}