package com.Logisight.jexception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	
	 // Genel hata kodları
    SOMETHING_WENT_WRONG("0001", "Bir hata oluştu."),
    INVALID_REQUEST("0002", "Geçersiz istek."),
    UNAUTHORIZED("0003", "Yetkisiz erişim."),
    INTERNAL_SERVER_ERROR("0004", "Sunucu hatası oluştu."),
    VALIDATION_FAILED("0005", "Doğrulama hatası."),

    // Kullanıcıya özel hata kodları (UserManager ile direkt ilgili)
    USER_NOT_FOUND("1001", "Kullanıcı bulunamadı."),
    USERNAME_ALREADY_EXISTS("1002", "Bu kullanıcı adı zaten kayıtlı."),
    EMAIL_ALREADY_EXISTS("1003", "Bu e-posta adresi zaten kayıtlı."),
    USER_CREATION_FAILED("1004", "Kullanıcı oluşturulurken hata oluştu."),
    USER_UPDATE_FAILED("1005", "Kullanıcı güncellenirken hata oluştu."),
    USER_DELETION_FAILED("1006", "Kullanıcı silinirken hata oluştu."),

    // Rol, yetki, kimlik (ilerideki servisler için temel)
    ROLE_NOT_FOUND("2001", "Rol bulunamadı."),
    ROLE_ALREADY_EXISTS("2002", "Bu rol zaten mevcut."),
    
    // Sistem yapılandırması (SystemConfig vs için)
    CONFIG_KEY_NOT_FOUND("3001", "Konfigürasyon anahtarı bulunamadı."),
    CONFIG_KEY_ALREADY_EXISTS("3002", "Bu konfigürasyon anahtarı zaten mevcut."),
    
    // Session, Anomali gibi domainler için örnek hata
    SESSION_NOT_FOUND("4001", "Oturum bulunamadı."),
    ANOMALY_NOT_FOUND("5001", "Anomali kaydı bulunamadı."),
    SESSION_ASSOCIATED_USER_NOT_FOUND("4002", "Oturuma ait kullanıcı bulunamadı."),
    SESSION_CREATION_FAILED("4003", "Oturum oluşturulurken hata oluştu."),
    SESSION_UPDATE_FAILED("4004", "Oturum güncellenirken hata oluştu."),
    SESSION_DELETION_FAILED("4005", "Oturum silinirken hata oluştu."),
    SESSION_ALREADY_LOGGED_OUT("4006","Bu oturum zaten çevrimdışı"),
    
    ANOMALY_CREATION_FAILED("5002", "Anomali oluşturulurken hata oluştu."),
    ANOMALY_UPDATE_FAILED("5003", "Anomali güncellenirken hata oluştu."),
    ANOMALY_DELETION_FAILED("5004", "Anomali silinirken hata oluştu."),
	
	 USER_ACTION_NOT_FOUND("6001", "Kullanıcı aksiyonu bulunamadı."),
	    USER_ACTION_CREATION_FAILED("6002", "Kullanıcı aksiyonu oluşturulurken hata oluştu."),
	    USER_ACTION_UPDATE_FAILED("6003", "Kullanıcı aksiyonu güncellenirken hata oluştu."),
	    USER_ACTION_DELETION_FAILED("6004", "Kullanıcı aksiyonu silinirken hata oluştu."),
	    USER_ACTION_INVALID_DATE_RANGE("6005", "Geçersiz tarih aralığı."),
	    USER_ACTION_ASSOCIATED_USER_NOT_FOUND("6006", "Aksiyona ait kullanıcı bulunamadı."),
	    USER_ACTION_ASSOCIATED_ANOMALY_NOT_FOUND("6007", "Aksiyona ait anomali bulunamadı."),
	    USER_ACTION_DUPLICATE("6008", "Bu kullanıcı aksiyonu zaten mevcut."),
	    
	    PDF_REPORT_NOT_FOUND("7001", "PDF raporu bulunamadı."),
	    PDF_REPORT_CREATION_FAILED("7002", "PDF raporu oluşturulurken hata oluştu."),
	    PDF_REPORT_UPDATE_FAILED("7003", "PDF raporu güncellenirken hata oluştu."),
	    PDF_REPORT_DELETION_FAILED("7004", "PDF raporu silinirken hata oluştu."),
	    PDF_REPORT_INVALID_DATE_RANGE("7005", "Geçersiz tarih aralığı."), 
	    PDF_REPORT_ASSOCIATED_USER_NOT_FOUND("7006","pdf rapor ile bağlantılı kullanıcı bulunamadı"),
	    PDF_REPORT_TYPE_NOT_FOUND("7007","pdf Rapor türü bulunamadı"),
	    
	    NOTIFICATION_NOT_FOUND("8001", "Bildirim bulunamadı."),
	    NOTIFICATION_CREATION_FAILED("8002", "Bildirim oluşturulurken hata oluştu."),
	    NOTIFICATION_UPDATE_FAILED("8003", "Bildirim güncellenirken hata oluştu."),
	    NOTIFICATION_DELETION_FAILED("8004", "Bildirim silinirken hata oluştu."),
	    NOTIFICATION_RECIPIENT_NOT_FOUND("8005", "Bildirim alıcısı bulunamadı.");
	
	
	
	
	    private final String code;
	    private final String message;

	    ErrorCode(String code, String message) {
	        this.code = code;
	        this.message = message;
	    }
	}

