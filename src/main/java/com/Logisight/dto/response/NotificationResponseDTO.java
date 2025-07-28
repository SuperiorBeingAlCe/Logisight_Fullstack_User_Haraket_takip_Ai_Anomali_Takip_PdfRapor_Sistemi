package com.Logisight.dto.response;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {

    private Long id;

    private String message;

    private LocalDateTime createdAt;

    private Boolean read;

    private Long recipientId;

    private String recipientUsername;

    private String link;
}
