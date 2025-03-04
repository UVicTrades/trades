package ca.uvictrades.trades.controller.trade.requests

import java.math.BigDecimal
import java.math.BigInteger

data class PlaceStockOrderRequest(
	val stock_id: String,
	val is_buy: Boolean,
	val order_type: StockOrderType,
	val quantity: BigInteger,
	val price: BigDecimal?,
) {
	enum class StockOrderType {
		LIMIT,
		MARKET,
	}
}

//{
//  "stock_id": 1,
//  "is_buy": true,
//  "order_type": "LIMIT",
//  "quantity": 100,
//  "price": 80
//}
