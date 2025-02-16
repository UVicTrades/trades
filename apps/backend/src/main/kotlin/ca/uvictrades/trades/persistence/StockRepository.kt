package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Stock.Companion.STOCK
import ca.uvictrades.trades.model.public.tables.records.StockRecord
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDING
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

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

}
