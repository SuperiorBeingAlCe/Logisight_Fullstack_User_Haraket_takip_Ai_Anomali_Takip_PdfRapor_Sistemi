package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.Logisight.service.abstracts.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionManager implements SessionService {

    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SessionMapper mapper;

    @Override
    public boolean isSessionActive(String sessionId) {
        return sessionRepo.existsBySessionIdAndActiveTrue(sessionId);
    }
    
    @Override
    public Optional<SessionResponseDTO> getBySessionId(String sessionId) {
        return sessionRepo.findBySessionId(sessionId)
            .map(mapper::toResponseDto);
    }
    
    @Override
    public SessionResponseDTO createSession(CreateSessionDTO createDTO) {
        User user = userRepo.findById(createDTO.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_ASSOCIATED_USER_NOT_FOUND));

        Session entity = mapper.toEntity(createDTO);
        entity.setUser(user);

        Session saved;
        try {
            saved = sessionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SESSION_CREATION_FAILED, ex.getMessage());
        }

        return mapper.toResponseDto(saved);
    }

    @Override
    @CachePut(value = "sessions", key = "#id")
    public Optional<SessionResponseDTO> updateSession(Long id, UpdateSessionDTO updateDTO) {
        Session entity = sessionRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        mapper.updateEntity(updateDTO, entity);

        Session updated;
        try {
            updated = sessionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SESSION_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }
    
    @Override
    @CachePut(value = "sessions", key = "#sessionId")
    public Optional<SessionResponseDTO> updateSession(String sessionId, UpdateSessionDTO updateDTO) {
        Session entity = sessionRepo.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        // DTO'dan entity'ye güncelleme mapper ile yapılıyor
        mapper.updateEntity(updateDTO, entity);

        Session updated;
        try {
            updated = sessionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SESSION_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    @Cacheable(value = "sessions", key = "#id")
    public Optional<SessionResponseDTO> getSessionById(Long id) {
        return sessionRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "sessionsBySessionId", key = "#sessionId")
    public Optional<SessionResponseDTO> getSessionBySessionId(String sessionId) {
        return sessionRepo.findBySessionId(sessionId)
            .map(mapper::toResponseDto);
    }

    // Sayfalı ve cache'lenmiş aktif sessionlar
    @Override
    @Cacheable(value = "activeSessionsByUser", key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<SessionResponseDTO> getActiveSessionsByUserId(Long userId, Pageable pageable) {
        return sessionRepo.findByUserIdAndActiveTrue(userId, pageable)
                .map(mapper::toResponseDto);
    }

    // Sayfalı ve cache'lenmiş tüm sessionlar kullanıcı bazında
    @Override
    @Cacheable(value = "allSessionsByUser", key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<SessionResponseDTO> getSessionsByUserId(Long userId, Pageable pageable) {
        return sessionRepo.findByUserId(userId, pageable)
                .map(mapper::toResponseDto);
    }

    // Sayfalı ve cache'lenmiş süresi dolmuş aktif sessionlar
    @Override
    @Cacheable(value = "expiredActiveSessions", key = "{#now.toString(), #pageable.pageNumber, #pageable.pageSize}")
    public Page<SessionResponseDTO> getExpiredActiveSessions(LocalDateTime now, Pageable pageable) {
        return sessionRepo.findByExpiredAtBeforeAndActiveTrue(now, pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @CacheEvict(value = {
            "sessions", "sessionsBySessionId", "activeSessionsByUser", "allSessionsByUser", "expiredActiveSessions"
        }, allEntries = true)
    public void deleteSession(Long id) {
        if (!sessionRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        try {
            sessionRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SESSION_DELETION_FAILED, ex.getMessage());
        }
    }
}
