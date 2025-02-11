package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Wallet.Companion.WALLET
import ca.uvictrades.trades.model.public.tables.records.WalletRecord
import ca.uvictrades.trades.model.public.tables.WalletTx.Companion.WALLET_TX
import ca.uvictrades.trades.model.public.tables.records.WalletTxRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class WalletRepository(
    private val create: DSLContext,
) {

    fun getWalletByWalletId(walletId: Int): WalletRecord? =
        create.selectFrom(WALLET)
            .where(WALLET.WALLET_ID.eq(walletId))
            .fetchOne()

    fun getWalletByUsername(username: String): WalletRecord? =
        create.selectFrom(WALLET)
            .where(WALLET.USERNAME.eq(username))
            .fetchOne()

    fun createWallet(
        username: String,
        balance: BigDecimal,
    ): WalletRecord {
        val record = create.newRecord(WALLET).also {
            it.username = username
            it.balance = balance
        }

        record.store()

        return record
    }

    fun getTransactionsByUsername(username: String): List<WalletTxRecord> {
        return create.select(WALLET_TX)
            .where(WALLET.USERNAME.eq(username))
            .fetch() //type of each entry is a Result<Record>
            .map{ it.into(WalletTxRecord::class.java)} //map into a WalletTxRecord
    }

}