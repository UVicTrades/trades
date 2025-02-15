package ca.uvictrades.trades.matching.port

interface MatchingService {
	fun place(order: SellLimitOrder)
	fun place(order: BuyMarketOrder): PlaceBuyOrderResult
}
