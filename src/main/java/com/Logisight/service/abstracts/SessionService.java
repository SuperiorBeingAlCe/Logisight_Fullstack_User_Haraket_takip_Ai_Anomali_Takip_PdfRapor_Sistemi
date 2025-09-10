package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;

public interface SessionService {
	 SessionResponseDTO createSession(CreateSessionDTO createDTO);

	    Optional<SessionResponseDTO> updateSession(Long id, UpdateSessionDTO updateDTO);

	    Optional<SessionResponseDTO> getSessionById(Long id);

	    Optional<SessionResponseDTO> getSessionBySessionId(String sessionId);

	    // Pageable ile sayfalı aktif sessionlar
	    Page<SessionResponseDTO> getActiveSessionsByUserId(Long userId, Pageable pageable);

	    // Pageable ile sayfalı tüm sessionlar
	    Page<SessionResponseDTO> getSessionsByUserId(Long userId, Pageable pageable);

	    // Pageable ile sayfalı süresi dolmuş ve aktif sessionlar
	    Page<SessionResponseDTO> getExpiredActiveSessions(LocalDateTime now, Pageable pageable);

	    void deleteSession(Long id);
	    
	    Optional<SessionResponseDTO> getBySessionId(String sessionId);
	    
	    Optional<SessionResponseDTO> updateSession(String sessionId, UpdateSessionDTO updateDTO);
	    
	    boolean isSessionActive(String sessionId);
	}