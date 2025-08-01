package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<SessionResponseDTO> getSessionById(Long id) {
        return sessionRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    public Optional<SessionResponseDTO> getSessionBySessionId(String sessionId) {
        return sessionRepo.findBySessionId(sessionId)
            .map(mapper::toResponseDto);
    }

    @Override
    public List<SessionResponseDTO> getActiveSessionsByUserId(Long userId) {
        return sessionRepo.findByUserIdAndActiveTrue(userId)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponseDTO> getSessionsByUserId(Long userId) {
        return sessionRepo.findByUserId(userId)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponseDTO> getExpiredActiveSessions(LocalDateTime now) {
        return sessionRepo.findByExpiredAtBeforeAndActiveTrue(now)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
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
