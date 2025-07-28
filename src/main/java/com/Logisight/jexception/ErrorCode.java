package com.Logisight.jexception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	  SOMETHING_DOES_NOT_EXIST("0001", "Kayıt bulunamadı."),
	    INVALID_REQUEST("0002", "Geçersiz istek."),
	    UNAUTHORIZED("0003", "Yetkisiz erişim."),
	    INTERNAL_SERVER_ERROR("0004", "Sunucu hatası oluştu."),
	    VALIDATION_FAILED("0005", "Doğrulama hatası."),
	    // Diğer kodlar eklenebilir.

	    ;

	    private final String code;
	    private final String message;

	    ErrorCode(String code, String message) {
	        this.code = code;
	        this.message = message;
	    }
	}