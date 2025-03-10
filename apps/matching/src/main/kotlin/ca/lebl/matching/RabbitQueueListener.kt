package ca.lebl.matching

import ca.lebl.matching.matchingservice.port.SellLimitOrder
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitQueueListener(
    rabbitTemplate: RabbitTemplate,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["matching.rpc.requests"])
    fun receiveRequest(message: SellLimitOrder) {
        logger.info("received sell limit order with stock id {}", message.stock)
    }

}