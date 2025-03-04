package ca.uvictrades.trades.controller.stock.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.math.BigInteger
import java.time.OffsetDateTime

data class GetStockTransactionsResponse(
	val success: Boolean = true,
	val data: List<ResponseElement>,
) {
	data class ResponseElement(
		val stock_tx_id: String,
		val parent_stock_tx_id: String?,
		val stock_id: String,
		val wallet_tx_id: String?,
		val order_status: OrderStatus,
		@field:JsonProperty("is_buy")
		val buy: Boolean,
		val order_type: OrderType,
		val stock_price: Int,
		val quantity: BigInteger,
		val time_stamp: OffsetDateTime,
	)

	enum class OrderStatus {
		PARTIALLY_COMPLETE,
		IN_PROGRESS,
		COMPLETED,
		CANCELLED,
	}

	enum class OrderType {
		MARKET,
		LIMIT,
	}
}

//{
//  "stock_tx_id": "<googleCompStockTxId>",
//  "parent_stock_tx_id": null,
//  "stock_id": "<googleStockId>",
//  "wallet_tx_id": null,
//  "order_status": "IN_PROGRESS",
//  "is_buy": false,
//  "order_type": "LIMIT",
//  "stock_price": 135,
//  "quantity": 550,
//  "time_stamp": "<timestamp>"
//}
