package com.Logisight.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnomalyResponseDTO {

    private Long id;

    private String anomalyType;

    private String description;

    private LocalDateTime detectedAt;

    private Long userId;

    private String username;

    private boolean resolved;

    private LocalDateTime resolvedAt;

    private List<Long> relatedUserActionIds;
}
