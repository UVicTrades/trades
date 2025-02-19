package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.records.WalletTransactionRecord
import ca.uvictrades.trades.model.public.tables.references.WALLET_TRANSACTION
import org.jooq.DSLContext
import org.jooq.impl.DSL.coalesce
import org.jooq.impl.DSL.sum
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class WalletRepository(
    private val create: DSLContext,
) {

    @Transactional
    fun creditOrDebitTrader(username: String, amount: BigDecimal) {
        val currentBalance = getWalletBalance(username)

        if (currentBalance + amount < BigDecimal.ZERO) {
            error("This operation would bring the trader's balance below zero.")
        }

        val transaction = create.newRecord(WALLET_TRANSACTION).apply {
            trader = username
            this.amount = amount
        }

        transaction.store()
    }

    fun getWalletBalance(username: String): BigDecimal {
        return with(WALLET_TRANSACTION) {
            create.select(coalesce(sum(AMOUNT), BigDecimal.ZERO))
                .from(WALLET_TRANSACTION)
                .where(TRADER.eq(username))
                .fetchOne()?.value1()
                ?: error("Couldn't get wallet balance for some reason...")
        }
    }

    fun addWalletTransaction(trader: String, amount: BigDecimal, buyOrder: Int) {
        val record = create.newRecord(WALLET_TRANSACTION).apply {
			this.buyOrderId = buyOrder
			this.trader = trader
			this.amount = amount
        }

		record.store()
    }

	fun getWalletTransactionsAssociatedWithBuyOrder(forTrader: String): List<WalletTransactionRecord> {
		return with(WALLET_TRANSACTION) {
			create.selectFrom(this)
				.where(TRADER.eq(forTrader))
				.and(BUY_ORDER_ID.isNotNull)
				.fetch()
		}
	}
}
