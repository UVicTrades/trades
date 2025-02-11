package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class MatchingService {

	private val sellOrders: MutableMap<String, PriorityQueue<SellLimitOrder>> = mutableMapOf()

	fun place(order: SellLimitOrder) {

		val stockSellOrders = getSellOrdersForStock(order.stock)

		stockSellOrders.add(order)
	}

	private fun getSellOrdersForStock(stock: String) =
		when (val existingQueue = sellOrders.get(stock)) {
			null -> {
				createNewStockQueue().also {
					sellOrders[stock] = it
				}
			}
			else -> existingQueue
		}

	private fun createNewStockQueue(): PriorityQueue<SellLimitOrder> {
		return PriorityQueue(compareBy(SellLimitOrder::pricePerUnit))
	}

	fun place(order: BuyMarketOrder): PlaceBuyOrderResult {

		val match = getSellOrdersForStock(order.stock).firstOrNull()

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