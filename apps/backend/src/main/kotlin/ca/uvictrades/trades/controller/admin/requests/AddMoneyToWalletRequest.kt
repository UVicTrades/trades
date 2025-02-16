package ca.uvictrades.trades.controller.admin.requests

import java.math.BigDecimal

data class AddMoneyToWalletRequest (
	val amount: BigDecimal,
)
