package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.math.min

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
		return PriorityQueue(compareBy(SellLimitOrder::pricePerUnit, SellLimitOrder::timestamp))
	}

	fun place(order: BuyMarketOrder): PlaceBuyOrderResult {

		var remainingUnits = order.quantity
		var remainingLiquidity = order.liquidity

		val residues: MutableList<SellLimitOrder> = mutableListOf()

		while (remainingUnits > 0) {
			val matchedOrder: SellLimitOrder = getSellOrdersForStock(order.stock).poll()
				?: // No more sell orders remain.
				return PlaceBuyOrderResult.Failure

			val matchedQuantity = min(matchedOrder.quantity, remainingUnits)
			remainingUnits -= matchedQuantity
			val matchedPrice = BigDecimal(matchedQuantity) * matchedOrder.pricePerUnit

			remainingLiquidity -= matchedPrice

			if (remainingLiquidity < BigDecimal.ZERO) {
				return PlaceBuyOrderResult.Failure
			}

			val residue = SellLimitOrder(
				matchedOrder.id,
				matchedOrder.stock,
				remainingUnits,
				matchedOrder.pricePerUnit,
				Instant.now(),
			)

			residues.add(residue)
		}

		return PlaceBuyOrderResult.Success(residues)
	}

}