package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.SellOrderRecord
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.references.SELL_ORDER
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDING
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class StockRepository(
	private val create: DSLContext,
) {

	fun createStock(name: String): StockRecord {
		val stock = create.newRecord(STOCK).apply {
			this.name = name
		}

		stock.store()

		return stock
	}

	fun addStockToUser(username: String, stockId: Int, quantity: Int) {
		with(STOCK_HOLDING) {
			create.insertInto(this)
				.set(TRADER, username)
				.set(STOCK, stockId)
				.set(QUANTITY, quantity)
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
		quantityToSubtract: Int
	) {
		val existingQuantity = getPortfolio(username)
			.find { it.stockId == stockId }
			?.quantity ?: 0

		if (existingQuantity - quantityToSubtract < 0) {
			error("This operation would result in the trader having negative holdings in stock $stockId")
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
			.fetch()
			.map {
				PortfolioItem(
					it.value1()!!,
					it.value2()!!,
					it.value3()!!,
				)
			}
	}

	fun getSellOrders(forIds: List<Int>): List<SellOrderRecord> {
		return with (SELL_ORDER) {
			create.selectFrom(this)
				.where(ID.`in`(forIds))
				.fetch()
		}
	}

}

data class PortfolioItem(
	val stockId: Int,
	val stockName: String,
	val quantity: Int,
)

data class SellOrderToTrader(
	val sellOrderId: Int,
	val trader: String,
)
