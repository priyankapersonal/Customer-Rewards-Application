package com.infy.exception;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler to catch and respond to exceptions across all
 * controllers.
 * <p>
 * Provides centralized handling for custom exceptions, validation errors, and
 * generic exceptions. Returns appropriate HTTP status codes and error details
 * in the response body.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles CustomerNotFoundException with HTTP 404 Not Found.
	 *
	 * @param ex      the exception thrown
	 * @param request the web request context
	 * @return ResponseEntity with error details and 404 status
	 */
	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ErrorDetails> handleCustomerNotFoundException(CustomerNotFoundException ex,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles InvalidRequestException with HTTP 400 Bad Request.
	 *
	 * @param ex      the exception thrown
	 * @param request the web request context
	 * @return ResponseEntity with error details and 400 status
	 */
	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorDetails> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles InvalidDateFormatException with HTTP 400 Bad Request.
	 *
	 * @param ex      the exception thrown
	 * @param request the web request context
	 * @return ResponseEntity with error details and 400 status
	 */
	@ExceptionHandler(InvalidDateFormatException.class)
	public ResponseEntity<ErrorDetails> handleInvalidDateFormatException(InvalidDateFormatException ex,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles validation errors from @Valid annotated DTOs.
	 *
	 * @param ex      the MethodArgumentNotValidException thrown
	 * @param request the web request context
	 * @return ResponseEntity with a map of field errors and 400 status
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Fallback handler for unhandled exceptions (HTTP 500).
	 *
	 * @param ex      the exception thrown
	 * @param request the web request context
	 * @return ResponseEntity with error details and 500 status
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGenericException(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles missing or malformed request body (HTTP 400).
	 *
	 * @param ex      the exception thrown
	 * @param request the web request context
	 * @return ResponseEntity with error details and 400 status
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), "Customer data is missing",
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}