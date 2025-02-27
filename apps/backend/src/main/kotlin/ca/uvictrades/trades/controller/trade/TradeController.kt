package ca.uvictrades.trades.controller.trade

import ca.uvictrades.trades.configuration.JwtVerifier
import ca.uvictrades.trades.controller.shared.SuccessTrueDataNull
import ca.uvictrades.trades.controller.trade.requests.CancelStockTransactionRequest
import ca.uvictrades.trades.service.TradeService
import ca.uvictrades.trades.controller.trade.requests.PlaceStockOrderRequest
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.server.ResponseStatusException

@RestController
class TradeController(
	private val tradeService: TradeService,
	private val jwtVerifier: JwtVerifier,
) {

	@PostMapping("/engine/placeStockOrder")
	fun placeStockOrder(
		@RequestHeader("token") token: String,
		@RequestBody body: PlaceStockOrderRequest
	): SuccessTrueDataNull {
		val username = jwtVerifier.verify(token)
		if(
			!(body.is_buy && body.order_type == PlaceStockOrderRequest.StockOrderType.MARKET && body.price == null)
				&& !(!body.is_buy && body.order_type == PlaceStockOrderRequest.StockOrderType.LIMIT && body.price != null)
		) { throw IllegalArgumentException("Bad combo of sell/buy market/limit") }

		if (body.is_buy) {
			tradeService.placeBuyOrder(
				username,
				body.stock_id.toInt(),
				body.quantity,
			)
		} else {
			tradeService.placeSellOrder(
				username,
				body.stock_id.toInt(),
				body.quantity,
				body.price!!,
			)
		}

		return SuccessTrueDataNull()
	}

	@PostMapping("/engine/cancelStockTransaction")
	fun cancelStockTransaction(
		@RequestHeader("token") token: String,
		@RequestBody body: CancelStockTransactionRequest
	): SuccessTrueDataNull {
		val username = jwtVerifier.verify(token)

		tradeService.cancelOrder(body.stock_tx_id.toInt(), username)

		return SuccessTrueDataNull()
	}

}
