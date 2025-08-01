package com.Logisight.dto.create;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAnomalyDTO {

    @NotBlank(message = "Anomaly tipi boş olamaz.")
    private String anomalyType;  // BOT_BEHAVIOR, BRUTE_FORCE, RAPID_REQUESTS vb.

    @NotBlank(message = "Açıklama boş olamaz.")
    private String description;

    @NotNull(message = "Tespit zamanı boş olamaz.")
    private LocalDateTime detectedAt;

    @NotNull(message = "Kullanıcı ID boş olamaz.")
    private Long userId;

    private boolean resolved;

    private LocalDateTime resolvedAt;
    }
