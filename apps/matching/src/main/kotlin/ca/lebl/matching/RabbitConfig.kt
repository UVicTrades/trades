package ca.lebl.matching

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

	@Bean
	@Qualifier("sellOrder")
	fun ueue(): Queue {
		return Queue("matching.rpc.sellOrder")
	}

	@Bean
	@Qualifier("buyOrder")
	fun buyOrderQueue(): Queue {
		return Queue("matching.rpc.buyOrder")
	}

	@Bean
	@Qualifier("getStockPrices")
	fun getStockPricesQueue(): Queue {
		return Queue("matching.rpc.getStockPrices")
	}

	@Bean
	@Qualifier("cancelStockOrder")
	fun cancelStockOrderQueue(): Queue {
		return Queue("matching.rpc.cancelStockOrder")
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
	fun getPricesBinding(
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
