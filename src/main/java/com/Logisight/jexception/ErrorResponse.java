package com.Logisight.jexception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
	@AllArgsConstructor
	public class ErrorResponse {

	    private String errorCode;
	    private String message;
	    private String details;  // opsiyonel, debug için
	}
	