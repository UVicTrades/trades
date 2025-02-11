package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.PriorityQueue
import kotlin.time.times

@Component
class MatchingService {

	private val sellOrders = PriorityQueue(compareBy(SellLimitOrder::pricePerUnit))

	fun place(order: SellLimitOrder) {
		sellOrders.add(order)
	}

	fun place(order: BuyMarketOrder): PlaceBuyOrderResult {
		val match = sellOrders.find {
			it.stock == order.stock
		}

		return if (match != null) {
			if (order.liquidity < BigDecimal(order.quantity) * match.pricePerUnit) {
				PlaceBuyOrderResult.Failure
			} else {

				if (match.quantity < order.quantity) {
					PlaceBuyOrderResult.Failure
				} else {
					PlaceBuyOrderResult.Success(match.id)
				}
			}
		} else {
			PlaceBuyOrderResult.Failure
		}
	}

}