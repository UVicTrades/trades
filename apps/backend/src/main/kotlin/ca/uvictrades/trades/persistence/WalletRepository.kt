package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.references.WALLET_TRANSACTION
import org.jooq.DSLContext
import org.jooq.impl.DSL.sum
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class WalletRepository(
    private val create: DSLContext,
) {

    fun addMoneyToTrader(username: String, amount: BigDecimal) {
        val transaction = create.newRecord(WALLET_TRANSACTION).apply {
            trader = username
            this.amount = amount
            hidden = true
        }

        transaction.store()
    }

    fun getWalletBalance(username: String): BigDecimal {
        return with(WALLET_TRANSACTION) {
            create.select(sum(AMOUNT))
                .from(WALLET_TRANSACTION)
                .where(TRADER.eq(username))
                .fetchOne()?.value1()
                ?: error("Couldn't get wallet balance for some reason...")
        }
    }
}
