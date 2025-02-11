package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class MatchingServiceTest {

	private lateinit var matchingService: MatchingService

	@BeforeEach
	fun setUp() {
		matchingService = MatchingService()
	}

	@Test
	fun `accept sell limit order`() {
		val order = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
		)

		matchingService.place(order)
	}

	@Test
	fun `accept buy order market`() {
		val order = BuyMarketOrder("aapl", 1, BigDecimal(9000.0))

		val result: PlaceBuyOrderResult = matchingService.place(order)
	}

	@Test
	fun `without sell orders, a buy order fails`() {
		val order = BuyMarketOrder("aapl", 1, BigDecimal(9000.0))

		val result = matchingService.place(order)

		assert(result is PlaceBuyOrderResult.Failure)
	}

	@Test
	fun `with a sell order, a buy order is matched`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)

		Assertions.assertEquals(sellOrder.id, result.matchedSellOrderId)
	}

	@Test
	fun `only orders for same stock can be matched`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "goog",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		assert(result is PlaceBuyOrderResult.Failure)
	}

	@Test
	fun `matching returns correct id`() {
		val sellOrder = SellLimitOrder(
			id = "some-specific-id",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)

		assertEquals("some-specific-id", result.matchedSellOrderId)
	}

	@Test
	fun `lower priced sell orders matched with priority`() {
		val lowSellOrder = SellLimitOrder(
			id = "low",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
		)

		val highSellOrder = SellLimitOrder(
			id = "high",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(200.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(highSellOrder)
		matchingService.place(lowSellOrder)
		matchingService.place(highSellOrder)

		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)

		assertEquals(lowSellOrder.id, result.matchedSellOrderId)
	}

	@Test
	fun `buy order fails with insufficient liquidity`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 1,
			liquidity = BigDecimal(90.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		assert(result is PlaceBuyOrderResult.Failure)
	}

	@Test
	fun `sell orders with insufficient quantity cannot be matched`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			2,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		assert(result is PlaceBuyOrderResult.Failure)
	}
}