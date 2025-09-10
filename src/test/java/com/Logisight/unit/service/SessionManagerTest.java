package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.entity.Session;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.SessionMapper;
import com.Logisight.repository.SessionRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.concretes.SessionManager;

@ExtendWith(MockitoExtension.class)
public class SessionManagerTest {
	
	@InjectMocks
    private SessionManager sessionManager;

    @Mock
    private SessionRepository sessionRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private SessionMapper mapper;

    private Session session;
    private SessionResponseDTO responseDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        session = new Session();
        session.setId(1L);
        session.setSessionId("sess-123");
        session.setUser(user);
        session.setActive(true);
        session.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        session.setExpiredAt(LocalDateTime.now().plusHours(1));

        responseDto = new SessionResponseDTO();
        responseDto.setId(session.getId());
        responseDto.setSessionId(session.getSessionId());
        responseDto.setUserId(user.getId());
        responseDto.setActive(session.isActive());
        responseDto.setCreatedAt(session.getCreatedAt());
        responseDto.setExpiredAt(session.getExpiredAt());
    }

    @Test
    void isSessionActive_true() {
        when(sessionRepo.existsBySessionIdAndActiveTrue("sess-123")).thenReturn(true);

        boolean active = sessionManager.isSessionActive("sess-123");

        assertTrue(active);
    }

    @Test
    void getBySessionId_found() {
        when(sessionRepo.findBySessionId("sess-123")).thenReturn(Optional.of(session));
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        Optional<SessionResponseDTO> result = sessionManager.getBySessionId("sess-123");

        assertTrue(result.isPresent());
        assertEquals("sess-123", result.get().getSessionId());
    }

    @Test
    void createSession_success() {
        CreateSessionDTO createDTO = new CreateSessionDTO();
        createDTO.setUserId(user.getId());
        createDTO.setSessionId("sess-123");
        createDTO.setActive(true);
        createDTO.setCreatedAt(LocalDateTime.now());
        createDTO.setExpiredAt(LocalDateTime.now().plusHours(1));

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toEntity(createDTO)).thenReturn(session);
        when(sessionRepo.save(session)).thenReturn(session);
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        SessionResponseDTO result = sessionManager.createSession(createDTO);

        assertNotNull(result);
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void updateSession_byId_success() {
        UpdateSessionDTO updateDTO = new UpdateSessionDTO();
        updateDTO.setActive(false);

        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));
        doAnswer(invocation -> {
            session.setActive(updateDTO.isActive());
            return null;
        }).when(mapper).updateEntity(updateDTO, session);
        when(sessionRepo.save(session)).thenReturn(session);
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        Optional<SessionResponseDTO> result = sessionManager.updateSession(1L, updateDTO);

        assertTrue(result.isPresent());
        assertFalse(session.isActive());
    }

    @Test
    void updateSession_bySessionId_success() {
        UpdateSessionDTO updateDTO = new UpdateSessionDTO();
        updateDTO.setActive(false);

        when(sessionRepo.findBySessionId("sess-123")).thenReturn(Optional.of(session));
        doAnswer(invocation -> {
            session.setActive(updateDTO.isActive());
            return null;
        }).when(mapper).updateEntity(updateDTO, session);
        when(sessionRepo.save(session)).thenReturn(session);
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        Optional<SessionResponseDTO> result = sessionManager.updateSession("sess-123", updateDTO);

        assertTrue(result.isPresent());
        assertFalse(session.isActive());
    }

    @Test
    void getSessionById_found() {
        when(sessionRepo.findById(1L)).thenReturn(Optional.of(session));
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        Optional<SessionResponseDTO> result = sessionManager.getSessionById(1L);

        assertTrue(result.isPresent());
        assertEquals("sess-123", result.get().getSessionId());
    }

    @Test
    void getSessionBySessionId_found() {
        when(sessionRepo.findBySessionId("sess-123")).thenReturn(Optional.of(session));
        when(mapper.toResponseDto(session)).thenReturn(responseDto);

        Optional<SessionResponseDTO> result = sessionManager.getSessionBySessionId("sess-123");

        assertTrue(result.isPresent());
        assertEquals("sess-123", result.get().getSessionId());
    }

    @Test
    void deleteSession_success() {
        when(sessionRepo.existsById(1L)).thenReturn(true);

        sessionManager.deleteSession(1L);

        verify(sessionRepo).deleteById(1L);
    }

    @Test
    void deleteSession_notFound() {
        when(sessionRepo.existsById(2L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> sessionManager.deleteSession(2L));

        assertEquals(ErrorCode.SESSION_NOT_FOUND, ex.getErrorCode());
    }
}
