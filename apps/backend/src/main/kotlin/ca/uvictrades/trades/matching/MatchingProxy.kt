package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.MatchingService
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class MatchingProxy(
	private val rabbitTemplate: RabbitTemplate,
	template: RabbitTemplate,
): MatchingService {

	private val logger = LoggerFactory.getLogger(this::class.java)

	override fun place(order: SellLimitOrder) {
		rabbitTemplate.convertSendAndReceive("matching.rpc.requests", order)
	}

	override fun place(order: BuyMarketOrder): PlaceBuyOrderResult {
		TODO("Not yet implemented")
	}

	override fun getStockPrices(): Map<String, BigDecimal> {
		TODO("Not yet implemented")
	}

	override fun cancelOrder(withId: String, forStock: String) {
		TODO("Not yet implemented")
	}
}
