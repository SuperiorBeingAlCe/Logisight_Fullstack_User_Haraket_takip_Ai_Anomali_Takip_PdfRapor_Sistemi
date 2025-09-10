package com.Logisight.jexception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	 @ExceptionHandler(BusinessException.class)
	    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
	        ErrorResponse error = new ErrorResponse(
	                ex.getErrorCode().getCode(),
	                ex.getMessage(),
	                null
	        );
	        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
	        String details = ex.getBindingResult().getFieldErrors().stream()
	                .map(FieldError::getDefaultMessage)
	                .collect(Collectors.joining("; "));
	        ErrorResponse error = new ErrorResponse(
	                ErrorCode.VALIDATION_FAILED.getCode(),
	                ErrorCode.VALIDATION_FAILED.getMessage(),
	                details
	        );
	        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
	        ErrorResponse error = new ErrorResponse(
	                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
	                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
	                ex.getLocalizedMessage()
	        );
	        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
}
