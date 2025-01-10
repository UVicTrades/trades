package ca.uvictrades.trades

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TradesApplication

fun main(args: Array<String>) {
	runApplication<TradesApplication>(*args)
}
