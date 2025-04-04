package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import ca.uvictrades.trades.matching.port.SellLimitOrderResidue
import ca.uvictrades.trades.matching.port.MatchingService as MatchingServiceInterface
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.min

@Component
class MatchingService : MatchingServiceInterface {

	private val sellOrders: MutableMap<String, PriorityQueue<SellLimitOrder>> = mutableMapOf()
	private val lock = ReentrantLock(true)

	override fun place(order: SellLimitOrder) {
		try {
			lock.lock()

			return internalPlaceSell(order)
		} finally {
		    lock.unlock()
		}
	}

	private fun internalPlaceSell(order: SellLimitOrder) {

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

		try {
			lock.lock()

			return internalPlaceOrder(order)
		} finally {
		    lock.unlock()
		}
	}

	private fun internalPlaceOrder(order: BuyMarketOrder): PlaceBuyOrderResult {

		var remainingUnits = order.quantity
		var remainingLiquidity = order.liquidity

		val residues: MutableList<SellLimitOrderResidue> = mutableListOf()
		val contingency: Queue<SellLimitOrder> = LinkedList()
		val selfTrade: Queue<SellLimitOrder> = LinkedList()

		while (remainingUnits > BigInteger.ZERO) {
			val matchedOrder: SellLimitOrder? = getSellOrdersForStock(order.stock).poll()

			if (matchedOrder == null) {
				reinsertSellOrders(contingency, getSellOrdersForStock(order.stock))
				reinsertSellOrders(selfTrade, getSellOrdersForStock(order.stock))
				return PlaceBuyOrderResult.Failure
			}

			if(order.trader == matchedOrder.trader) {
				contingency.add(matchedOrder)
				selfTrade.add(matchedOrder)
				continue
			}

			contingency.add(matchedOrder)

			val matchedQuantity = matchedOrder.quantity.min(remainingUnits)
			remainingUnits -= matchedQuantity
			val matchedPrice = BigDecimal(matchedQuantity) * matchedOrder.pricePerUnit

			remainingLiquidity -= matchedPrice

			if (remainingLiquidity < BigDecimal.ZERO) {
				reinsertSellOrders(contingency, getSellOrdersForStock(order.stock))
				reinsertSellOrders(selfTrade, getSellOrdersForStock(order.stock))
				return PlaceBuyOrderResult.Failure
			}

			val residue = SellLimitOrderResidue(
				orderId = matchedOrder.id,
				stockId = matchedOrder.stock,
				matchedQuantity = matchedQuantity,
				remainingQuantity = matchedOrder.quantity - matchedQuantity,
				pricePerUnit = matchedOrder.pricePerUnit,
				timestamp = matchedOrder.timestamp,
				trader = matchedOrder.trader,
			)

			residues.add(residue)
		}

		residues.filter { it.remainingQuantity > BigInteger.ZERO }
			.forEach { residue ->
				getSellOrdersForStock(order.stock).add(
					SellLimitOrder(
						residue.orderId,
						residue.stockId,
						residue.remainingQuantity,
						residue.pricePerUnit,
						residue.timestamp,
						residue.trader,
					)
				)
			}

		reinsertSellOrders(selfTrade, getSellOrdersForStock(order.stock))

		return PlaceBuyOrderResult.Success(residues, remainingLiquidity)
	}

	private fun reinsertSellOrders(fromContingencyQueue: Queue<SellLimitOrder>, into: PriorityQueue<SellLimitOrder>) {
		generateSequence { fromContingencyQueue.poll() }
			.forEach {
				into.add(it)
			}
	}

	override fun getStockPrices(): Map<String, BigDecimal> {
		return sellOrders.map { (stockId, queue) ->
			stockId to (queue.peek()?.pricePerUnit ?: BigDecimal.ZERO)
		}
			.filter { (_, price) -> price > BigDecimal.ZERO }
			.toMap()
	}

	override fun cancelOrder(withId: String, forStock: String) {
		try {
		    lock.lock()

			internalCancelOrder(withId, forStock)
		} finally {
		    lock.unlock()
		}
	}

	private fun internalCancelOrder(withId: String, forStock: String) {
		val stockQueue = sellOrders[forStock] ?: error("$forStock does not exist")

		val (idMatch, idNotMatch) = generateSequence { stockQueue.poll() }
			.partition { it.id == withId }

		assert(idMatch.isNotEmpty()) { "did not find a sell order with id $withId" }
		assert(idMatch.count() == 1) { "for some reason, there were more than one matching sell orders with id $withId" }

		generateSequence { idNotMatch.firstOrNull() }
			.forEach {
				stockQueue.add(it)
			}
	}

}
