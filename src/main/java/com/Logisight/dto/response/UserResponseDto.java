package com.Logisight.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
	   private Long id;

	    private String username;

	    private String email;

	    private boolean enabled;

	    private LocalDateTime createdAt;

	    private LocalDateTime updatedAt;

	    private Set<String> roles;
}
