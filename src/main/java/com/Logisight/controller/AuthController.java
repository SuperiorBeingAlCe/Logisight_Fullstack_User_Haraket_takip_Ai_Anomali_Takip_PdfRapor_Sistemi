package com.Logisight.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.dto.request.LoginRequestDTO;
import com.Logisight.dto.response.LoginResponseDTO;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.service.abstracts.AuthService;
import com.Logisight.service.abstracts.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
private final AuthService authService;

private final UserService userService;

@PostMapping("/login")
public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request,
        HttpServletRequest httpRequest) {
LoginResponseDTO response = authService.login(request, httpRequest);
return ResponseEntity.ok(response);
}

@GetMapping("/me")
public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    return userService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PostMapping("/logout")
public ResponseEntity<Void> logout(@RequestParam String sessionId, HttpServletRequest httpRequest) {
    authService.logout(sessionId, httpRequest);
    return ResponseEntity.ok().build();
}
}
