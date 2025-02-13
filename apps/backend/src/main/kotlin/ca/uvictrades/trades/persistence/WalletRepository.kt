package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Trader.Companion.TRADER
import ca.uvictrades.trades.model.public.tables.records.TraderRecord
import ca.uvictrades.trades.model.public.tables.WalletTx.Companion.WALLET_TX
import ca.uvictrades.trades.model.public.tables.records.WalletTxRecord
import ca.uvictrades.trades.model.public.tables.references.STOCK_HOLDINGS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class WalletRepository(
    private val create: DSLContext,
) {

    fun getWalletByUsername(username: String): TraderRecord? =
        create.selectFrom(TRADER)
            .where(TRADER.USERNAME.eq(username))
            .fetchOne()

    fun getTransactionsByUsername(username: String): List<WalletTxRecord> {
        return create.selectFrom(WALLET_TX)
            .where(WALLET_TX.USERNAME.eq(username))
            .fetch() //type of each entry is a Result<Record>
            .map { it.into(WalletTxRecord::class.java) } //map into a WalletTxRecord
    }

    fun addMoneyToWallet(username: String, amount: BigDecimal){
        with(TRADER) {
            create.update(TRADER)
                .set(BALANCE, TRADER.BALANCE.plus(amount))
                .where(USERNAME.eq(username))
                .execute()
        }
    }

    fun newTransaction(username: String, stock_tx_id: Int?, amount: BigDecimal, isDebit: Boolean) {
        val record = create.newRecord(WALLET_TX).also {
            it.username = username
            it.stockTxId = stock_tx_id
            it.isDebit = isDebit
            it.amount = amount
        }

        record.store()
    }

}