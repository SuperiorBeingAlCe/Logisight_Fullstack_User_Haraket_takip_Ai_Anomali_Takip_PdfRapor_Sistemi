package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;

public interface SessionService {
	SessionResponseDTO createSession(CreateSessionDTO createDTO);

    Optional<SessionResponseDTO> updateSession(Long id, UpdateSessionDTO updateDTO);

    Optional<SessionResponseDTO> getSessionById(Long id);

    Optional<SessionResponseDTO> getSessionBySessionId(String sessionId);

    List<SessionResponseDTO> getActiveSessionsByUserId(Long userId);

    List<SessionResponseDTO> getSessionsByUserId(Long userId);

    List<SessionResponseDTO> getExpiredActiveSessions(LocalDateTime now);

    void deleteSession(Long id);

}
