package ca.uvictrades.trades.service

import ca.uvictrades.trades.model.public.tables.records.TraderRecord
import ca.uvictrades.trades.persistence.TraderRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class TraderService(
    private val traderRepo: TraderRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun registerNewTrader(
        username: String,
        password: String,
        name: String,
    ): TraderRecord {

        val encodedPassword = passwordEncoder.encode(password)
        return traderRepo.createTrader(username, encodedPassword, name)

    }

    fun loginTrader(
        username: String,
        password: String,
    ): TraderRecord {
        val trader = traderRepo.getTrader(username) ?: throw IllegalArgumentException("No Trader found for $username")

        if (passwordEncoder.matches(password, trader.password)) {
            return trader
        } else {
            throw IllegalArgumentException("Invalid credentials")
        }
    }

}