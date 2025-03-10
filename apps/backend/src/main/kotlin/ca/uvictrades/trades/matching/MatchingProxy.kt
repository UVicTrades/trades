package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.MatchingService
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MatchingProxy(
	private val rabbitTemplate: RabbitTemplate,
): MatchingService {

	private val logger = LoggerFactory.getLogger(this::class.java)

	override fun place(order: SellLimitOrder) {
		rabbitTemplate.convertSendAndReceive("matching.rpc.sellOrder.${order.stock}", order)
	}

	override fun place(order: BuyMarketOrder): PlaceBuyOrderResult {
		val type = object: ParameterizedTypeReference<PlaceBuyOrderResult>() {}
		return rabbitTemplate.convertSendAndReceiveAsType("matching.rpc.buyOrder.${order.stock}", order, type)!!
	}

	override fun getStockPrices(): Map<String, BigDecimal> {
		TODO("Not yet implemented")

		val map = mutableMapOf<String, BigDecimal>()
		val stockIds = listOf(1, 2)

		for (id in stockIds) {

		}

		return map
	}

	override fun cancelOrder(withId: String, forStock: String) {
		TODO("Not yet implemented")
	}
}
