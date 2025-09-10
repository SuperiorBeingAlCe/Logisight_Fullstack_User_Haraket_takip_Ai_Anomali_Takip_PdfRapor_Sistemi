package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.entity.UserAction;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.UserActionMapper;
import com.Logisight.repository.AnomalyRepository;
import com.Logisight.repository.UserActionRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.UserActionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserActionManager implements UserActionService {

    private final UserActionRepository actionRepo;
    private final UserRepository userRepo;
    private final AnomalyRepository anomalyRepo;
    private final UserActionMapper mapper;

    @Override
    public UserActionResponseDto createUserAction(CreateUserActionDTO dto) {
        User user = userRepo.findById(dto.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_ACTION_ASSOCIATED_USER_NOT_FOUND));
         
        Anomaly anomaly = null;
        if (dto.getAnomalyId() != null) {
            anomaly = anomalyRepo.findById(dto.getAnomalyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_ACTION_ASSOCIATED_ANOMALY_NOT_FOUND));
        }

        UserAction entity = mapper.toEntity(dto);
        entity.setUser(user);
        entity.setAnomaly(anomaly);

        UserAction saved;
        try {
            saved = actionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_ACTION_CREATION_FAILED, ex.getMessage());
        }

        return mapper.toResponseDto(saved);
    }

    @Override
    public Optional<UserActionResponseDto> updateUserAction(Long id, UpdateUserActionDTO dto) {
        UserAction entity = actionRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_ACTION_NOT_FOUND));

        mapper.updateEntity(dto, entity);
        if (dto.getActionTimestamp() != null) {
            entity.setActionTimestamp(dto.getActionTimestamp());
        }

        UserAction updated;
        try {
            updated = actionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_ACTION_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }
    
    @Override
    public Page<UserActionResponseDto> getAllUserActions(Pageable pageable) {
        return actionRepo.findAll(pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "userActions", key = "#id")
    public Optional<UserActionResponseDto> getUserActionById(Long id) {
        return actionRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    // Sayfalama destekli:
    @Override
    public Page<UserActionResponseDto> getUserActionsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (start.isAfter(end)) {
            throw new BusinessException(ErrorCode.USER_ACTION_INVALID_DATE_RANGE);
        }
        return actionRepo.findByUserIdAndActionTimestampBetween(userId, start, end, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    public Page<UserActionResponseDto> getUserActionsByIpAddressAndDateRange(String ipAddress, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (start.isAfter(end)) {
            throw new BusinessException(ErrorCode.USER_ACTION_INVALID_DATE_RANGE);
        }
        return actionRepo.findByIpAddressAndActionTimestampBetween(ipAddress, start, end, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "userActionsBySession", key = "#sessionId")
    public Page<UserActionResponseDto> getUserActionsBySessionId(String sessionId, Pageable pageable) {
        return actionRepo.findBySessionId(sessionId, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "userActionsByAnomaly", key = "#anomalyId")
    public Page<UserActionResponseDto> getUserActionsByAnomalyId(Long anomalyId, Pageable pageable) {
        return actionRepo.findByAnomalyId(anomalyId, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    public Long countUserActionsSince(Long userId, LocalDateTime since) {
        return actionRepo.countByUserIdAndActionTimestampAfter(userId, since);
    }

    @Override
    @Cacheable(value = "userActionStats", key = "'groupedByType'")
    public List<Object[]> countActionsGroupedByType() {
        return actionRepo.countGroupedByActionType();
    }

    @Override
    @CacheEvict(value = {
        "userActions",
        "userActionsBySession",
        "userActionsByAnomaly",
        "userActionStats"
    }, allEntries = true)
    public void deleteUserAction(Long id) {
        if (!actionRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_ACTION_NOT_FOUND);
        }
        try {
            actionRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_ACTION_DELETION_FAILED, ex.getMessage());
        }
    }
}
