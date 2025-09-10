package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.request.LoginRequestDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.response.LoginResponseDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.service.abstracts.AnomalyService;
import com.Logisight.service.abstracts.SessionService;
import com.Logisight.service.abstracts.UserActionService;
import com.Logisight.service.concretes.AuthManager;
import com.Logisight.service.concretes.UserDetailsImpl;
import com.Logisight.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

public class AuthManagerTest {
	
	 @InjectMocks
	    private AuthManager authManager;

	    @Mock
	    private AuthenticationManager authenticationManager;

	    @Mock
	    private UserDetailsService userDetailsService;

	    @Mock
	    private JwtUtil jwtUtil;

	    @Mock
	    private SessionService sessionService;
	    
	    @Mock
	    private AnomalyService anomalyService;

	    @Mock
	    private UserActionService userActionService;

	    @Mock
	    private HttpServletRequest httpRequest;

	    private UserDetailsImpl userDetails;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);

	        userDetails = new UserDetailsImpl(
	                1L, 
	                "testuser", 
	                "password", 
	                Collections.singleton(() -> "ROLE_USER")
	        );

	        // Clock sabitle
	        Clock fixedClock = Clock.fixed(
	            LocalDateTime.of(2025, 1, 1, 21, 30)
	                .atZone(ZoneId.systemDefault())
	                .toInstant(),
	            ZoneId.systemDefault()
	        );

	        ReflectionTestUtils.setField(authManager, "clock", fixedClock);
	    }
	    
	    @Test
	    void login_success() {
	        LoginRequestDTO loginRequest = new LoginRequestDTO();
	        loginRequest.setUsername("testuser");
	        loginRequest.setPassword("password");

	        Authentication auth = mock(Authentication.class);
	        when(auth.getPrincipal()).thenReturn(userDetails);
	        when(authenticationManager.authenticate(any())).thenReturn(auth);
	        when(httpRequest.getHeader("User-Agent")).thenReturn("JUnit-Agent");
	        when(jwtUtil.getExpirationMillis()).thenReturn(3600000L);
	        when(sessionService.createSession(any())).thenAnswer(i -> {
	            CreateSessionDTO dto = i.getArgument(0);
	            SessionResponseDTO response = new SessionResponseDTO();
	            response.setSessionId(dto.getSessionId());
	            response.setUserId(dto.getUserId());
	            response.setCreatedAt(dto.getCreatedAt());
	            response.setExpiredAt(dto.getExpiredAt());
	            response.setActive(dto.isActive());
	            return response;
	        });
	        when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("fake-jwt-token");
	        when(httpRequest.getHeader("User-Agent")).thenReturn("JUnit-Agent");

	        LoginResponseDTO response = authManager.login(loginRequest, httpRequest);

	        assertNotNull(response);
	        assertEquals("fake-jwt-token", response.getToken());
	        assertEquals(userDetails.getId(), response.getUserId());
	        verify(userActionService, times(1)).createUserAction(any());
	    }

	    @Test
	    void logout_success() {
	        String sessionId = UUID.randomUUID().toString();
	        LocalDateTime now = LocalDateTime.now();

	        SessionResponseDTO session = new SessionResponseDTO();
	        session.setSessionId(sessionId);
	        session.setUserId(1L);
	        session.setActive(true);
	        session.setCreatedAt(now.minusMinutes(5));

	        when(sessionService.getBySessionId(sessionId)).thenReturn(Optional.of(session));
	        when(httpRequest.getHeader("User-Agent")).thenReturn("JUnit-Agent");
	        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

	        when(sessionService.updateSession(eq(sessionId), any(UpdateSessionDTO.class)))
	        .thenAnswer(invocation -> {
	            UpdateSessionDTO dto = invocation.getArgument(1);
	            session.setActive(dto.isActive());
	            return Optional.of(session);
	        });
	        
	        authManager.logout(sessionId, httpRequest);

	        assertFalse(session.isActive());
	        verify(sessionService, times(1)).updateSession(eq(sessionId), any());
	        verify(userActionService, times(1)).createUserAction(any());
	    }

	    @Test
	    void logout_sessionNotFound() {
	        String sessionId = "nonexistent";

	        when(sessionService.getBySessionId(sessionId)).thenReturn(Optional.empty());

	        BusinessException ex = assertThrows(BusinessException.class, () -> authManager.logout(sessionId, httpRequest));
	        assertEquals(ErrorCode.SESSION_NOT_FOUND, ex.getErrorCode());
	    } 

	    @Test
	    void logout_alreadyLoggedOut() {
	        String sessionId = UUID.randomUUID().toString();

	        SessionResponseDTO session = new SessionResponseDTO();
	        session.setActive(false);

	        when(sessionService.getBySessionId(sessionId)).thenReturn(Optional.of(session));

	        BusinessException ex = assertThrows(BusinessException.class, () -> authManager.logout(sessionId, httpRequest));
	        assertEquals(ErrorCode.SESSION_ALREADY_LOGGED_OUT, ex.getErrorCode());
	    }
	    @Test
	    void login_lateLogin_createsAnomaly() {
	        // Arrange
	        LoginRequestDTO loginRequest = new LoginRequestDTO();
	        loginRequest.setUsername("testuser");
	        loginRequest.setPassword("password");

	        Authentication auth = mock(Authentication.class);
	        when(auth.getPrincipal()).thenReturn(userDetails);
	        when(authenticationManager.authenticate(any())).thenReturn(auth);
	        when(httpRequest.getHeader("User-Agent")).thenReturn("JUnit-Agent");
	        when(jwtUtil.getExpirationMillis()).thenReturn(3600000L);
	        when(sessionService.createSession(any())).thenAnswer(i -> {
	            CreateSessionDTO dto = i.getArgument(0);
	            SessionResponseDTO response = new SessionResponseDTO();
	            response.setSessionId(dto.getSessionId());
	            response.setUserId(dto.getUserId());
	            response.setCreatedAt(dto.getCreatedAt());
	            response.setExpiredAt(dto.getExpiredAt());
	            response.setActive(dto.isActive());
	            return response;
	        });
	        when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("fake-jwt-token");

	        // 1️⃣ Anomaly mock
	        AnomalyResponseDTO anomalyResponse = new AnomalyResponseDTO();
	        anomalyResponse.setId(999L);
	        when(anomalyService.createAnomaly(any())).thenReturn(anomalyResponse);

	        // 2️⃣ Clock override / sahte zaman (gerekirse Login methodunu parametre ile override edebilirsin)
	        LocalDateTime lateLoginTime = LocalDateTime.of(2025, 1, 1, 21, 30);
	        
	        // Eğer login methodun LocalDateTime.now() kullanıyorsa burada Clock veya injection ile sahte zaman geçmek gerekebilir.
	        // Alternatif olarak AuthManager login methoduna LocalDateTime parametresi ekleyip test için geçebilirsin.

	        // Act
	        LoginResponseDTO response = authManager.login(loginRequest, httpRequest);

	        // Assert
	        assertNotNull(response);
	        verify(anomalyService, times(1)).createAnomaly(any());

	        // userActionService.createUserAction çağrısındaki DTO’yu yakalayalım
	        ArgumentCaptor<CreateUserActionDTO> captor = ArgumentCaptor.forClass(CreateUserActionDTO.class);
	        verify(userActionService).createUserAction(captor.capture());
	        CreateUserActionDTO actionDto = captor.getValue();
	        
	        assertEquals(999L, actionDto.getAnomalyId());
	    }
}
