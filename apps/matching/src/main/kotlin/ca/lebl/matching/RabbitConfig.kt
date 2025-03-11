package ca.lebl.matching

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

	@Value("\${matching.rpc.queue.sell-order}")
	private lateinit var sellOrderQueueName: String

	@Value("\${matching.rpc.queue.buy-order:matching.rpc.buyOrder}")
	private lateinit var buyOrderQueueName: String

	@Value("\${matching.rpc.queue.get-stock-prices}")
	private lateinit var getStockPricesQueueName: String

	@Value("\${matching.rpc.queue.cancel-stock-order}")
	private lateinit var cancelStockOrderQueueName: String

	@Bean
	@Qualifier("sellOrder")
	fun sellOrderQueue(): Queue {
		return Queue(sellOrderQueueName)
	}

	@Bean
	@Qualifier("buyOrder")
	fun buyOrderQueue(): Queue {
		return Queue(buyOrderQueueName)
	}

	@Bean
	@Qualifier("getStockPrices")
	fun getStockPricesQueue(): Queue {
		return Queue(getStockPricesQueueName)
	}

	@Bean
	@Qualifier("cancelStockOrder")
	fun cancelStockOrderQueue(): Queue {
		return Queue(cancelStockOrderQueueName)
	}

	@Bean
	fun directExchange(): DirectExchange {
		return DirectExchange("matching.rpc")
	}

	@Bean
	fun sellOrderBinding(
		exchange: DirectExchange,
		@Qualifier("sellOrder")
		queue: Queue,
	): Binding {
		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with("rpc")
	}

	@Bean
	fun buyOrderBinding(
		exchange: DirectExchange,
		@Qualifier("buyOrder")
		queue: Queue,
	): Binding {
		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with("rpc")
	}

	@Bean
	fun getStockPricesBinding(
		exchange: DirectExchange,
		@Qualifier("getStockPrices")
		queue: Queue,
	): Binding {
		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with("rpc")
	}

	@Bean
	fun cancelOrderBinding(
		exchange: DirectExchange,
		@Qualifier("cancelStockOrder")
		queue: Queue,
	): Binding {
		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with("rpc")
	}

	@Bean
	fun producerJacksonToMessageConverter(): Jackson2JsonMessageConverter {
		return Jackson2JsonMessageConverter()
	}

	@Bean
	fun rabbitTemplate(
		connectionFactory: ConnectionFactory,
		messageConverter: Jackson2JsonMessageConverter,
	): RabbitTemplate {
		return RabbitTemplate(connectionFactory).apply {
			this.messageConverter = messageConverter
		}
	}

}
