package ca.uvictrades.trades.persistence

import ca.uvictrades.trades.model.public.tables.Trader.Companion.TRADER
import ca.uvictrades.trades.model.public.tables.records.TraderRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class TraderRepository(
    private val create: DSLContext,
) {

    fun getTrader(withUsername: String): TraderRecord? =
        create.selectFrom(TRADER)
            .where(TRADER.USERNAME.eq(withUsername))
            .fetchOne()

    fun createTrader(
        username: String,
        password: String,
        name: String,
    ): TraderRecord {
        val record = create.newRecord(TRADER).also {
            it.username = username
            it.password = password
            it.name = name
        }

        record.store()

        return record
    }

}