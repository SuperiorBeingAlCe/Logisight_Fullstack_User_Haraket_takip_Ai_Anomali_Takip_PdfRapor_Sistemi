package com.Logisight.dto.response;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionResponseDTO {

    private Long id;

    private String sessionId;

    private Long userId;

    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    private boolean active;

    private String ipAddress;

    private String userAgent;
}
