package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.SellOrderRecord
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.references.SELL_ORDER
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDING
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigInteger

@Repository
class StockRepository(
	private val create: DSLContext,
) {
	fun getStocksForIds(ids: Set<Int>): Set<StockRecord> {
		return create.selectFrom(STOCK)
			.where(STOCK.ID.`in`(ids))
			.orderBy(STOCK.NAME.desc())
			.fetch()
			.toSet()
	}

	fun createStock(name: String): StockRecord {
		val stock = create.newRecord(STOCK).apply {
			this.name = name
		}

		stock.store()

		return stock
	}

	fun addStockToUser(username: String, stockId: Int, quantity: BigInteger) {
		with(STOCK_HOLDING) {
			create.insertInto(this)
				.set(TRADER, username)
				.set(STOCK, stockId)
				.set(QUANTITY, quantity.toLong())
				.onConflict(TRADER, STOCK)
				.doUpdate()
				.set(QUANTITY, QUANTITY.plus(quantity))
				.execute()
		}
	}

	@Transactional
	fun subtractStockFromUser(
		username: String,
		stockId: Int,
		quantityToSubtract: BigInteger
	) {
		val existingQuantity = getPortfolio(username)
			.find { it.stockId == stockId }
			?.quantity ?: BigInteger.ZERO

		if (existingQuantity - quantityToSubtract < BigInteger.ZERO) {
			throw IllegalArgumentException("This operation would result in the trader having negative holdings in stock $stockId")
		}

		with(STOCK_HOLDING) {
			create.update(this)
				.set(QUANTITY, QUANTITY.minus(quantityToSubtract))
				.where(TRADER.eq(username))
				.and(STOCK.eq(stockId))
				.execute()
		}
	}

	fun getPortfolio(username: String): List<PortfolioItem> =
	with(STOCK_HOLDING) {
		create.select(
			STOCK,
			stock().NAME,
			QUANTITY,
		)
			.from(this)
			.where(TRADER.eq(username))
			.and(QUANTITY.gt(0))
			.orderBy(stock().NAME.desc())
			.fetch()
			.map {
				PortfolioItem(
					it.value1()!!,
					it.value2()!!,
					BigInteger.valueOf(it.value3()!!),
				)
			}
	}

	fun getSellOrders(forIds: List<Int>): List<SellOrderRecord> {
		return with (SELL_ORDER) {
			create.selectFrom(this)
				.where(ID.`in`(forIds))
				.orderBy(TIME_STAMP)
				.fetch()
		}
	}

}

data class PortfolioItem(
	val stockId: Int,
	val stockName: String,
	val quantity: BigInteger,
)

data class SellOrderToTrader(
	val sellOrderId: Int,
	val trader: String,
)
