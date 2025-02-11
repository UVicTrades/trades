package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.springframework.stereotype.Component

@Component
class MatchingService {

	private val sellOrders: MutableList<SellLimitOrder> = mutableListOf()

	fun place(order: SellLimitOrder) {
		sellOrders.add(order)
	}

	fun place(order: BuyMarketOrder): PlaceBuyOrderResult {
		val match = sellOrders.find {
			it.stock == order.stock
		}

		return if (match != null) {
			PlaceBuyOrderResult.Success(match.id)
		} else {
			PlaceBuyOrderResult.Failure
		}
	}

}