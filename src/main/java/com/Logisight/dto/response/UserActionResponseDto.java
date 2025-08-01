package com.Logisight.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserActionResponseDto {
	   private Long id;

	    private Long userId;

	    private String username;

	    private String sessionId;

	    private String actionType;

	    private String actionDetail;

	    private LocalDateTime actionTimestamp;

	    private Long durationMs;

	    private String ipAddress;

	    private String userAgent;

	    private Long anomalyId;

	    private String anomalyType;
}
