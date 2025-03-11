package ca.lebl.matching

import ca.lebl.matching.matchingservice.port.BuyMarketOrder
import ca.lebl.matching.matchingservice.port.MatchingService
import ca.lebl.matching.matchingservice.port.PlaceBuyOrderResult
import ca.lebl.matching.matchingservice.port.SellLimitOrder
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class RabbitQueueListener(
    private val matchingService: MatchingService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["#{'\${matching.rpc.queue.sell-order}'}"])
    fun receiveSellOrderRequest(message: SellLimitOrder) {
        logger.info("received sell limit order with stock id {}", message.stock)
        matchingService.place(message)
    }

    @RabbitListener(queues = ["#{'\${matching.rpc.queue.buy-order}'}"])
    fun receiveBuyOrderRequest(message: BuyMarketOrder): PlaceBuyOrderResult {
        logger.info("received buy order with stock id {}", message.stock)
        return matchingService.place(message)
    }

    @RabbitListener(queues = ["#{'\${matching.rpc.queue.get-stock-prices}'}"])
    fun receiveGetStockPricesRequest():  Map<String, BigDecimal> {
        logger.info("received get stock prices request")
        return matchingService.getStockPrices()
    }

    @RabbitListener(queues = ["#{'\${matching.rpc.queue.cancel-stock-order}'}"])
    fun receiveCancelStockOrderRequest(message: Map<String, String>) {
        val orderId = message["orderId"]
        val stockId = message["stockId"]

        if (orderId != null && stockId != null) {
            logger.info("received cancel stock order request with order id {} and stock id {}", orderId, stockId)
            matchingService.cancelOrder(orderId, stockId)
        } else {
            logger.warn("invalid cancel stock order request: {}", message)
        }
    }
}