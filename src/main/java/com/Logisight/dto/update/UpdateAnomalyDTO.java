package com.Logisight.dto.update;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAnomalyDTO {
    @NotNull(message = "Durum boş olamaz")
    private Boolean resolved;    
    private LocalDateTime resolvedAt;
}
