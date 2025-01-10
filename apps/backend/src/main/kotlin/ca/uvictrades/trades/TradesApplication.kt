package ca.uvictrades.trades

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class TradesApplication

fun main(args: Array<String>) {
	runApplication<TradesApplication>(*args)
}
