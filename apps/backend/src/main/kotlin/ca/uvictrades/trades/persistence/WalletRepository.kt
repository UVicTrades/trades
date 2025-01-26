package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Wallet.Companion.WALLET
import ca.uvictrades.trades.model.public.tables.records.WalletRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class WalletRepository(
    private val create: DSLContext,
) {
    fun getWalletByUsername(username: String): WalletRecord? =
        create.selectFrom(WALLET)
            .where(WALLET.USERNAME.eq(username))
            .fetchOne()

    fun createWallet(
        walletId: Int,
        username: String,
        balance: BigDecimal,
    ): WalletRecord {
        val record = create.newRecord(WALLET).also {
            it.wallet_id = wallet_id
            it.username = username
            it.balance = balance
        }

        record.store()

        return record
    }

}