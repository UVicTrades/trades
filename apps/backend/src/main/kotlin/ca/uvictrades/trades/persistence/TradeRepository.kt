package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.SellOrder.Companion.SELL_ORDER
import ca.uvictrades.trades.model.public.tables.records.BuyOrderRecord
import ca.uvictrades.trades.model.public.tables.BuyOrder.Companion.BUY_ORDER
import ca.uvictrades.trades.model.public.tables.records.BuyOrderSellOrderRecord
import ca.uvictrades.trades.model.public.tables.records.SellOrderRecord
import ca.uvictrades.trades.model.public.tables.references.BUY_ORDER_SELL_ORDER
import ca.uvictrades.trades.model.public.tables.references.WALLET_TRANSACTION
import ca.uvictrades.trades.persistence.SellOrderWithBuys.BuyOrder
import org.jooq.DSLContext
import org.jooq.Records
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.jooq.kotlin.mapping
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

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

	fun cancelSellOrder(withId: Int) {
		with(SELL_ORDER) {
			create.update(this)
				.set(CANCELLED, true)
				.where(ID.eq(withId))
				.execute()
		}
	}

	fun getSellOrdersWithAssociatedBuys(forTrader: String): List<SellOrderWithBuys> {
		return create
			.select(
				SELL_ORDER.ID,
				SELL_ORDER.STOCK_ID,
				SELL_ORDER.QUANTITY,
				SELL_ORDER.PRICE_PER_SHARE,
				SELL_ORDER.TIME_STAMP,
				multiset(
					select(
						BUY_ORDER_SELL_ORDER.buyOrder().ID,
						BUY_ORDER_SELL_ORDER.buyOrder().QUANTITY,
						WALLET_TRANSACTION.ID,
						BUY_ORDER_SELL_ORDER.TIME_STAMP,
					)
						.from(BUY_ORDER_SELL_ORDER)
						.join(WALLET_TRANSACTION)
						.on(WALLET_TRANSACTION.BUY_ORDER_ID.eq(BUY_ORDER_SELL_ORDER.BUY_ORDER_ID))
						.where(WALLET_TRANSACTION.AMOUNT.gt(BigDecimal.ZERO))
						.and(BUY_ORDER_SELL_ORDER.SELL_ORDER_ID.eq(SELL_ORDER.ID))
				).convertFrom { it.map { record ->
					BuyOrder(
						record.value1()!!,
						record.value2()!!,
						record.value3()!!,
						record.value4()!!,
					)
				}},
				SELL_ORDER.CANCELLED,
			)
			.from(SELL_ORDER)
			.where(SELL_ORDER.TRADER.eq(forTrader))
			.fetch().map { record ->
				SellOrderWithBuys(
					record.value1()!!,
					record.value2()!!,
					record.value3()!!,
					record.value4()!!,
					record.value6(),
					record.value7()!!,
					record.value5()!!,
				)
			}
	}

	fun getBuyOrders(forTrader: String): List<BuyOrderWithStockIdAndWalletTxId> {
		return create
			.select(
				BUY_ORDER.ID,
				SELL_ORDER.STOCK_ID,
				BUY_ORDER.TRADER,
				BUY_ORDER.QUANTITY,
				WALLET_TRANSACTION.ID,
				SELL_ORDER.PRICE_PER_SHARE,
				BUY_ORDER.TIME_STAMP
			)
			.from(BUY_ORDER)
			.join(WALLET_TRANSACTION)
			.on(WALLET_TRANSACTION.BUY_ORDER_ID.eq(BUY_ORDER.ID))
			.join(BUY_ORDER_SELL_ORDER)
			.on(BUY_ORDER.ID.eq(BUY_ORDER_SELL_ORDER.BUY_ORDER_ID))
			.join(SELL_ORDER)
			.on(BUY_ORDER_SELL_ORDER.SELL_ORDER_ID.eq(SELL_ORDER.ID))
			.where(WALLET_TRANSACTION.AMOUNT.lt(BigDecimal.ZERO))
			.fetch().map { record ->
				BuyOrderWithStockIdAndWalletTxId(
					record.value1()!!,
					record.value2()!!,
					record.value3()!!,
					record.value4()!!,
					record.value5()!!,
					record.value6()!!,
					record.value7()!!,
				)
			}
	}
}

data class BuyOrderWithStockIdAndWalletTxId(
	val buyOrderId: Int,
	val stockId: Int,
	val trader: String,
	val quantity: Int,
	val walletTransactionId: Int,
	val pricePerShare: BigDecimal,
	val timestamp: LocalDateTime,
)

data class SellOrderQuantity(
	val sellOrderId: Int,
	val quantity: Int,
)

data class SellOrderWithBuys(
	val sellOrderId: Int,
	val stockId: Int,
	val quantity: Int,
	val pricePerShare: BigDecimal,
	val buyOrders: List<BuyOrder>,
	val isCancelled: Boolean,
	val timestamp: LocalDateTime,
) {
	data class BuyOrder(
		val buyOrderId: Int,
		val quantity: Int,
		val sellerWalletTransactionId: Int,
		val timestamp: LocalDateTime,
	)
}
