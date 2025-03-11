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
		val stockIds = listOf(1, 2)
		val combinedPrices = mutableMapOf<String, BigDecimal>()

		val typeRef = object : ParameterizedTypeReference<Map<String, BigDecimal>>() {}


		for (id in stockIds) {
			val routingKey = "matching.rpc.getStockPrices.$id"
			
			val response = rabbitTemplate.convertSendAndReceiveAsType(
				routingKey,
				"", // empty request
				typeRef
			)
			
			if (response != null) {
				combinedPrices.putAll(response)
			} else {
				logger.warn("No price response received from matching engine for shard $id")
			}
		}

		return combinedPrices
	}

	override fun cancelOrder(withId: String, forStock: String) {
		TODO("Not yet implemented")
	}
}
