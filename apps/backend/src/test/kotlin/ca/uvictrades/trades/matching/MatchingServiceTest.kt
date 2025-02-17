package ca.uvictrades.trades.matching

import ca.uvictrades.trades.matching.port.BuyMarketOrder
import ca.uvictrades.trades.matching.port.PlaceBuyOrderResult
import ca.uvictrades.trades.matching.port.SellLimitOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertContentEquals
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
			timestamp = Instant.now(),
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
			timestamp = Instant.now(),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)

		Assertions.assertEquals(sellOrder.id, result.sellOrderResidues.first().orderId)
	}

	@Test
	fun `only orders for same stock can be matched`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
			timestamp = Instant.now(),
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
			timestamp = Instant.now(),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 3,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrder)
		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)

		assertEquals("some-specific-id", result.sellOrderResidues.first().orderId)
	}

	@Test
	fun `lower priced sell orders matched with priority`() {
		val lowSellOrder = SellLimitOrder(
			id = "low",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(100.0),
			timestamp = Instant.now(),
		)

		val highSellOrder = SellLimitOrder(
			id = "high",
			stock = "aapl",
			quantity = 3,
			pricePerUnit = BigDecimal(200.0),
			timestamp = Instant.now(),
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

		assertEquals(lowSellOrder.id, result.sellOrderResidues.first().orderId)
	}

	@Test
	fun `buy order fails with insufficient liquidity`() {
		val sellOrder = SellLimitOrder(
			id = "id",
			stock = "aapl",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = Instant.now(),
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
			timestamp = Instant.now(),
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

	@Test
	fun `multiple sells can fulfull a single buy`() {
		val sellOrderOne = SellLimitOrder(
			id = "one",
			stock = "aapl",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = Instant.now(),
		)

		val sellOrderTwo = SellLimitOrder(
			id = "two",
			stock = "aapl",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = Instant.now(),
		)

		val buyOrder = BuyMarketOrder(
			stock = "aapl",
			quantity = 2,
			liquidity = BigDecimal(9000.0),
		)

		matchingService.place(sellOrderOne)
		matchingService.place(sellOrderTwo)
		val result = matchingService.place(buyOrder)

		require(result is PlaceBuyOrderResult.Success)
	}

	@Test
	fun `the most recent sell order breaks any price tie`() {
		val orderOne = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(20),
			timestamp = Instant.parse("2024-02-10T15:30:00Z")
		)

		val orderTwo = SellLimitOrder(
			id = "two",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(20),
			timestamp = Instant.parse("2024-02-10T16:00:00Z")
		)

		val orderThree = SellLimitOrder(
			id = "three",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(20),
			timestamp = Instant.parse("2024-02-10T16:30:00Z")
		)

		val orderFour = SellLimitOrder(
			id = "four",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(20),
			timestamp = Instant.parse("2024-02-10T17:00:00Z")
		)

		matchingService.place(orderTwo)
		matchingService.place(orderThree)
		matchingService.place(orderOne)
		matchingService.place(orderFour)

		val buy = BuyMarketOrder(
			"acme",
			quantity = 1,
			liquidity = BigDecimal(9000.0),
		)

		val result = matchingService.place(buy)

		require(result is PlaceBuyOrderResult.Success)

		assertEquals("one", result.sellOrderResidues.first().orderId)

		assertEquals(1, result.sellOrderResidues.size)

		assertEquals(0, result.sellOrderResidues.first().remainingQuantity)
	}

	@Test
	fun `multiple sells fulfilling one buy return correct residues`() {

		val time = Instant.now()

		val sellOne = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time
		)

		val sellTwo = SellLimitOrder(
			id = "two",
			stock = "acme",
			quantity = 4,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time.plusSeconds(1),
		)

		matchingService.place(sellOne)
		matchingService.place(sellTwo)

		val buy = BuyMarketOrder(
			stock = "acme",
			quantity = 2,
			liquidity = BigDecimal(9000.0),
		)

		val result = matchingService.place(buy)

		require(result is PlaceBuyOrderResult.Success)

		val residualQuantities = result.sellOrderResidues.associate {
			it.orderId to it.remainingQuantity
		}

		assertEquals(0, residualQuantities["one"])
		assertEquals(3, residualQuantities["two"])
	}

	@Test
	fun `successful buy returns residual liquidity`() {
		val time = Instant.now()

		val sellOne = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time
		)

		val sellTwo = SellLimitOrder(
			id = "two",
			stock = "acme",
			quantity = 4,
			pricePerUnit = BigDecimal(120.0),
			timestamp = time.plusSeconds(1),
		)

		matchingService.place(sellOne)
		matchingService.place(sellTwo)

		val buy = BuyMarketOrder(
			"acme",
			quantity = 2,
			liquidity = BigDecimal(9000.0),
		)

		// fulfilling this will require 1@100 and 1@120 = 220
		// 9000 - 220 = 8780

		val result = matchingService.place(buy)

		require(result is PlaceBuyOrderResult.Success)

		assertEquals(BigDecimal(8780), result.residualLiquidity)
	}

	@Test
	fun `failed buys do not affect the state of the engine`() {
		val time = Instant.now()

		val sellOne = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time,
		)

		val sellTwo = SellLimitOrder(
			id = "two",
			stock = "acme",
			quantity = 4,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time.plusSeconds(1),
		)

		val sellThree = SellLimitOrder(
			id = "three",
			stock = "acme",
			quantity = 2,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time.plusSeconds(2),
		)

		matchingService.place(sellOne)
		matchingService.place(sellTwo)
		matchingService.place(sellThree)

		val badBuyOrder = BuyMarketOrder(
			"acme",
			quantity = 3,
			BigDecimal(299.0)
		)

		val badResult = matchingService.place(badBuyOrder)

		assert(badResult is PlaceBuyOrderResult.Failure)

		val goodBuyOrder = BuyMarketOrder(
			"acme",
			quantity = 6,
			BigDecimal(9000.0),
		)

		val goodResult = matchingService.place(goodBuyOrder)

		require(goodResult is PlaceBuyOrderResult.Success) { "Expected PlaceBuyOrderResult.Success, got ${goodResult::class.simpleName}" }

		assertEquals(BigDecimal(9000-600), goodResult.residualLiquidity)

		val remainingStockPerOrder = goodResult.sellOrderResidues.associate {
			it.orderId to it.remainingQuantity
		}

		assertEquals(0, remainingStockPerOrder["one"])
		assertEquals(0, remainingStockPerOrder["two"])
		assertEquals(1, remainingStockPerOrder["three"])
	}

	@Test
	fun `matched and remaining quantities are correct`() {
		val time = Instant.now()

		val sellOne = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 1,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time,
		)

		val sellTwo = SellLimitOrder(
			id = "two",
			stock = "acme",
			quantity = 4,
			pricePerUnit = BigDecimal(100.0),
			timestamp = time.plusSeconds(1),
		)

		matchingService.place(sellOne)
		matchingService.place(sellTwo)

		val buy = BuyMarketOrder(
			"acme",
			quantity = 3,
			BigDecimal(9000.0),
		)

		val result = matchingService.place(buy)

		require(result is PlaceBuyOrderResult.Success)

		val remainingStockPerOrder = result.sellOrderResidues.associate {
			it.orderId to it.remainingQuantity
		}

		val matchedStockPerOrder = result.sellOrderResidues.associate {
			it.orderId to it.matchedQuantity
		}

		assertEquals(0, remainingStockPerOrder["one"])
		assertEquals(2, remainingStockPerOrder["two"])

		assertEquals(1, matchedStockPerOrder["one"])
		assertEquals(2, matchedStockPerOrder["two"])
	}

	@Test
	fun `a single sell can fulfill multiple buys`() {
		val time = Instant.now()

		val sellOrder = SellLimitOrder(
			id = "one",
			stock = "acme",
			quantity = 8,
			pricePerUnit = BigDecimal(100.0),
			time,
		)

		matchingService.place(sellOrder)

		val resultOne = matchingService.place(
			BuyMarketOrder(
				"acme",
				quantity = 3,
				BigDecimal(9000.0),
			)
		)

		assert(resultOne is PlaceBuyOrderResult.Success)

		val resultTwo = matchingService.place(
			BuyMarketOrder(
				"acme",
				quantity = 1,
				BigDecimal(9000.0),
			)
		)

		assert(resultTwo is PlaceBuyOrderResult.Success)

		val resultThree = matchingService.place(
			BuyMarketOrder(
				"acme",
				quantity = 4,
				BigDecimal(9000.0),
			)
		)

		assert(resultThree is PlaceBuyOrderResult.Success)

		val resultFour = matchingService.place(
			BuyMarketOrder(
				"acme",
				quantity = 1,
				BigDecimal(9000.0),
			)
		)

		assert(resultFour is PlaceBuyOrderResult.Failure)
	}
}
