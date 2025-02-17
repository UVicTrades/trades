package ca.uvictrades.trades.controller.stock.responses


import ca.uvictrades.trades.persistence.SellOrderWithStockName
import java.math.BigDecimal

data class GetStockPricesResponse(
	val success: Boolean = true,
	val data: List<DataElement>,
) {
	data class DataElement(
		val stock_id: String,
		val stock_name: String,
		val current_price: BigDecimal,
	)
}

fun SellOrderWithStockName.toGetStockPricesDataElement(): GetStockPricesResponse.DataElement {
	return GetStockPricesResponse.DataElement(
		stock_id = this.stockId.toString(),
		stock_name = this.name,
		current_price = this.pricePerShare,
	)
}
