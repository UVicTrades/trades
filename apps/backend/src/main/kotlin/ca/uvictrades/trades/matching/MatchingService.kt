package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import ca.uvictrades.trades.matching.port.MatchingService as MatchingServiceInterface
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.math.min

@Component
class MatchingService : MatchingServiceInterface {

	private val sellOrders: MutableMap<String, PriorityQueue<SellLimitOrder>> = mutableMapOf()

	override fun place(order: SellLimitOrder) {

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

	override fun place(order: BuyMarketOrder): PlaceBuyOrderResult {

		var remainingUnits = order.quantity
		var remainingLiquidity = order.liquidity

		val residues: MutableList<SellLimitOrder> = mutableListOf()
		val contingency: Queue<SellLimitOrder> = LinkedList()

		while (remainingUnits > 0) {
			val matchedOrder: SellLimitOrder? = getSellOrdersForStock(order.stock).poll()

			if (matchedOrder == null) {
				reinsertSellOrders(contingency, getSellOrdersForStock(order.stock))
				return PlaceBuyOrderResult.Failure
			}

			contingency.add(matchedOrder)

			val matchedQuantity = min(matchedOrder.quantity, remainingUnits)
			remainingUnits -= matchedQuantity
			val matchedPrice = BigDecimal(matchedQuantity) * matchedOrder.pricePerUnit

			remainingLiquidity -= matchedPrice

			if (remainingLiquidity < BigDecimal.ZERO) {
				reinsertSellOrders(contingency, getSellOrdersForStock(order.stock))
				return PlaceBuyOrderResult.Failure
			}

			val residue = SellLimitOrder(
				matchedOrder.id,
				matchedOrder.stock,
				matchedOrder.quantity - matchedQuantity,
				matchedOrder.pricePerUnit,
				Instant.now(),
			)

			residues.add(residue)
		}

		return PlaceBuyOrderResult.Success(residues, remainingLiquidity)
	}

	private fun reinsertSellOrders(fromContingencyQueue: Queue<SellLimitOrder>, into: PriorityQueue<SellLimitOrder>) {
		generateSequence { fromContingencyQueue.poll() }
			.forEach {
				into.add(it)
			}
	}

}
