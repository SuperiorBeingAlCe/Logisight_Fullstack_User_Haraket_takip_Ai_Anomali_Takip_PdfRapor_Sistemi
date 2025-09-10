package com.Logisight.dto.create;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSessionDTO {

    @NotBlank(message = "Session ID boş olamaz.")
    private String sessionId;

    @NotNull(message = "Kullanıcı ID boş olamaz.")
    private Long userId;

    @NotNull(message = "Oturum oluşturulma zamanı boş olamaz.")
    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @NotNull(message = "Aktiflik durumu belirtilmelidir.")
    private boolean active;

    private String ipAddress;

    private String userAgent;
}
