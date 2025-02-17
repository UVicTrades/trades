package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.SellOrder.Companion.SELL_ORDER
import ca.uvictrades.trades.model.public.tables.records.BuyOrderRecord
import ca.uvictrades.trades.model.public.tables.BuyOrder.Companion.BUY_ORDER
import ca.uvictrades.trades.model.public.tables.records.BuyOrderSellOrderRecord
import ca.uvictrades.trades.model.public.tables.records.SellOrderRecord
import ca.uvictrades.trades.model.public.tables.references.BUY_ORDER_SELL_ORDER
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class TradeRepository(
	private val create: DSLContext,
) {

	fun getMatchedBuyOrders(forSellOrderWithId: Int): List<BuyOrderRecord> {
		with(BUY_ORDER) {
			return create.selectFrom(this)
				.where(this.sellOrder.ID.eq(forSellOrderWithId))
//				.where(SELL_ORDER_ID.eq(forSellOrderWithId))
				.fetch()
		}
	}

	fun getSellOrder(withId: Int): SellOrderRecord {
		return with (SELL_ORDER) {
			create.selectFrom(this)
				.where(ID.eq(withId))
				.fetchOne() ?: error("Sell order not found.")
		}
	}

	fun  newBuyOrder(
		trader: String,
		sellOrderQuantities: List<SellOrderQuantity>,
	): BuyOrderRecord {
		val quantity = sellOrderQuantities.sumOf { it.quantity }

		val buyOrderRecord = create.newRecord(BUY_ORDER).apply {
			this.trader = trader
			this.quantity = quantity
		}

		buyOrderRecord.store()

		val sellOrderQuantityRecords = mutableListOf<BuyOrderSellOrderRecord>()

		sellOrderQuantities.forEach {
			val joinRecord = create.newRecord(BUY_ORDER_SELL_ORDER)

			joinRecord.quantity = it.quantity
			joinRecord.sellOrderId = it.sellOrderId
			joinRecord.buyOrderId = buyOrderRecord.id

			sellOrderQuantityRecords.add(joinRecord)
		}

		create.batchStore(sellOrderQuantityRecords).execute()

		return buyOrderRecord
	}

	fun newSellOrder(
		trader: String,
		stockId: Int,
		quantity: Int,
		pricePerUnit: BigDecimal,
	): SellOrderRecord {
		val record = create.newRecord(SELL_ORDER).apply {
			this.trader = trader
			this.stockId = stockId
			this.quantity = quantity
			this.pricePerShare = pricePerUnit
		}

		record.store()

		return record
	}

}

data class SellOrderQuantity(
	val sellOrderId: Int,
	val quantity: Int,
)
