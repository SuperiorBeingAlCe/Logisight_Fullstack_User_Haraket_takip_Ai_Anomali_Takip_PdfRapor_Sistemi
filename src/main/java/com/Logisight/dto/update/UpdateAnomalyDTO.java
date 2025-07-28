package com.Logisight.dto.update;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UpdateAnomalyDTO {

    private boolean resolved;

    private LocalDateTime resolvedAt;
}
