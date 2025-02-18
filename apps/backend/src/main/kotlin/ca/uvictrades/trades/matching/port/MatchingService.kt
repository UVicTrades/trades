package ca.uvictrades.trades.matching.port

import java.math.BigDecimal

interface MatchingService {
	fun place(order: SellLimitOrder)
	fun place(order: BuyMarketOrder): PlaceBuyOrderResult
	fun getStockPrices(): Map<String, BigDecimal>
}
