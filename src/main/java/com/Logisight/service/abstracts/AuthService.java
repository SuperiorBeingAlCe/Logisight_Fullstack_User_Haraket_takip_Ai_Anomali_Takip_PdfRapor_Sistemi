package com.Logisight.service.abstracts;

import com.Logisight.dto.request.LoginRequestDTO;
import com.Logisight.dto.response.LoginResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

	LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest);
	 void logout(String sessionId, HttpServletRequest httpRequest);
}
