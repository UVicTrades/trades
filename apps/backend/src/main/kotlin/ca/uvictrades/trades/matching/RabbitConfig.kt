package ca.uvictrades.trades.matching

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
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
