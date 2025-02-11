package ca.uvictrades.trades.service

import ca.uvictrades.trades.controller.responses.WalletTransactionResponse
import ca.uvictrades.trades.model.public.tables.records.WalletRecord
import ca.uvictrades.trades.persistence.WalletRepository
import ca.uvictrades.trades.model.public.tables.records.WalletTxRecord
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

    fun getWalletBalanceByUserName(username: String): WalletRecord? {
        return walletRepo.getWalletByUsername(username)
    }

    fun getWalletTransactions(username: String): List<WalletTransactionResponse> {
        return walletRepo.getTransactionsByUsername(username)
            .map { it.into(WalletTransactionResponse::class.java) }
    }
}