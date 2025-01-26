package ca.uvictrades.trades.service

import ca.uvictrades.trades.model.public.tables.records.WalletRecord
import ca.uvictrades.trades.persistence.WalletRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WalletService(
    private val walletRepo: WalletRepository,
) {

    fun addNewWallet(
        username: String,
        balance: BigDecimal,
    ): WalletRecord {

        return walletRepo.createWallet(username, balance)

    }

}