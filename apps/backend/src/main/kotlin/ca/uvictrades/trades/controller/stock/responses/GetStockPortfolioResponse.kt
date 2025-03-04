package ca.uvictrades.trades.controller.stock.responses

import ca.uvictrades.trades.persistence.PortfolioItem
import java.math.BigInteger

data class GetStockPortfolioResponse(
	val success: Boolean = true,
	val data: List<DataElement>,
) {
	data class DataElement(
		val stock_id: String,
		val stock_name: String,
		val quantity_owned: BigInteger,
	)
}

fun PortfolioItem.toGetPortfolioDataElement(): GetStockPortfolioResponse.DataElement {
	return GetStockPortfolioResponse.DataElement(
		stock_id = this.stockId.toString(),
		stock_name = stockName,
		quantity_owned = quantity
	)
}
