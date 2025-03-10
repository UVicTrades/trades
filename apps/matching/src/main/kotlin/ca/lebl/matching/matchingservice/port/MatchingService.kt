package ca.lebl.matching.matchingservice.port

import java.math.BigDecimal

interface MatchingService {
	fun place(order: SellLimitOrder)
	fun place(order: BuyMarketOrder): PlaceBuyOrderResult
	fun getStockPrices(): Map<String, BigDecimal>
	fun cancelOrder(withId: String, forStock: String)
}
