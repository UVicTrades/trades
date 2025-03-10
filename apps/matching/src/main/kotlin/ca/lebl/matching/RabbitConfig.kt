package ca.lebl.matching

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

	@Bean
	fun queue(): Queue {
		return Queue("matching.rpc.requests")
	}

	@Bean
	fun directExchange(): DirectExchange {
		return DirectExchange("matching.rpc")
	}

	@Bean
	fun binding(
		exchange: DirectExchange,
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
