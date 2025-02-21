package ca.uvictrades.trades.controller.shared

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<SuccessTrueDataNull> {
		val errorMessage = ex.bindingResult.fieldErrors
			.joinToString(", ") { it.defaultMessage ?: "Invalid request" }

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(SuccessTrueDataNull(success = false, data = SuccessTrueDataNull.Error(error = errorMessage)))
	}

	@ExceptionHandler(JwtException::class)
	fun handleJwtException(ex: JwtException): ResponseEntity<SuccessTrueDataNull> {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(SuccessTrueDataNull(success = false, data = SuccessTrueDataNull.Error(error = "Invalid or expired token")))
	}

	@ExceptionHandler(Exception::class)
	fun handleGeneralException(ex: Exception): ResponseEntity<SuccessTrueDataNull> {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(SuccessTrueDataNull(success = false, data = SuccessTrueDataNull.Error(error = "An unexpected error occurred")))
	}
}
