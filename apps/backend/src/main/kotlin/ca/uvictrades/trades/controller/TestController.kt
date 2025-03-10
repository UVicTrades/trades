package ca.uvictrades.trades.controller

import ca.uvictrades.trades.matching.port.MatchingService
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

@RestController
@RequestMapping("/test")
class TestController(
	private val matchingService: MatchingService,
) {

	@GetMapping
	fun testGet() {
		matchingService.place(SellLimitOrder(
			"id:1",
			"stock:1",
			BigInteger.ZERO,
			BigDecimal.ZERO,
			Instant.now(),
			"trader:matt"
		))
	}

}
