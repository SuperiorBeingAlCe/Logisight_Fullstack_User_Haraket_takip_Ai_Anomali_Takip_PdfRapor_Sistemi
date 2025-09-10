package com.Logisight.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
	
	private String token;
    private String sessionId;
    private Long userId;
    private String role;
    private LocalDateTime expiration;
}
