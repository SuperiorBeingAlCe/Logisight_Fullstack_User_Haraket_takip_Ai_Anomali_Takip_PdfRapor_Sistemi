package com.Logisight.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponseDto {
	   private Long id;

	    private String username;

	    private String email;

	    private boolean enabled;

	    private LocalDateTime createdAt;

	    private LocalDateTime updatedAt;

	    private Set<String> roles;
}
