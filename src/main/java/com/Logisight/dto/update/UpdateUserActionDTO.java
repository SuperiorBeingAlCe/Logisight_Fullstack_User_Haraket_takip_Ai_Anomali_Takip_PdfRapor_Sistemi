package com.Logisight.dto.update;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUserActionDTO {

    @NotBlank(message = "Oturum ID'si boş olamaz.")
    private String sessionId;

    @NotBlank(message = "Aksiyon tipi boş olamaz.")
    private String actionType;

    @NotBlank(message = "Aksiyon detayı boş olamaz.")
    @Size(max = 2048, message = "Aksiyon detayı en fazla 2048 karakter olabilir.")
    private String actionDetail;

    @NotNull(message = "Aksiyon zaman bilgisi boş olamaz.")
    private LocalDateTime actionTimestamp;

    @NotNull(message = "Süre bilgisi boş olamaz.")
    @PositiveOrZero(message = "Süre negatif olamaz.")
    private Long durationMs;

    @NotBlank(message = "IP adresi boş olamaz.")
    private String ipAddress;

    private String userAgent;

    private Long anomalyId;
}
