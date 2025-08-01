package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
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
        // İlişkili user kontrolü
        User user = userRepo.findById(dto.getUserId())
            .orElseThrow(() -> new BusinessException(
                ErrorCode.USER_ACTION_ASSOCIATED_USER_NOT_FOUND));
        // İlişkili anomaly kontrolü (opsiyonel)
        Anomaly anomaly = null;
        if (dto.getAnomalyId() != null) {
            anomaly = anomalyRepo.findById(dto.getAnomalyId())
                .orElseThrow(() -> new BusinessException(
                    ErrorCode.USER_ACTION_ASSOCIATED_ANOMALY_NOT_FOUND));
        }

        UserAction entity = mapper.toEntity(dto);
        entity.setUser(user);
        entity.setAnomaly(anomaly);

        UserAction saved;
        try {
            saved = actionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_CREATION_FAILED, ex.getMessage());
        }

        return mapper.toResponseDto(saved);
    }

    @Override
    public Optional<UserActionResponseDto> updateUserAction(Long id, UpdateUserActionDTO dto) {
        UserAction entity = actionRepo.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.USER_ACTION_NOT_FOUND));

        mapper.updateEntity(dto, entity);
        // Tarih güncellemesi varsa uygula
        if (dto.getActionTimestamp() != null) {
            entity.setActionTimestamp(dto.getActionTimestamp());
        }

        UserAction updated;
        try {
            updated = actionRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    public Optional<UserActionResponseDto> getUserActionById(Long id) {
        return actionRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    public List<UserActionResponseDto> getUserActionsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_INVALID_DATE_RANGE);
        }
        return actionRepo.findByUserIdAndActionTimestampBetween(userId, start, end)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserActionResponseDto> getUserActionsByIpAddressAndDateRange(String ipAddress, LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_INVALID_DATE_RANGE);
        }
        return actionRepo.findByIpAddressAndActionTimestampBetween(ipAddress, start, end)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserActionResponseDto> getUserActionsBySessionId(String sessionId) {
        return actionRepo.findBySessionId(sessionId)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserActionResponseDto> getUserActionsByAnomalyId(Long anomalyId) {
        return actionRepo.findByAnomalyId(anomalyId)
            .stream().map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public Long countUserActionsSince(Long userId, LocalDateTime since) {
        return actionRepo.countByUserIdAndActionTimestampAfter(userId, since);
    }

    @Override
    public List<Object[]> countActionsGroupedByType() {
        return actionRepo.countGroupedByActionType();
    }

    @Override
    public void deleteUserAction(Long id) {
        if (!actionRepo.existsById(id)) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_NOT_FOUND);
        }
        try {
            actionRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.USER_ACTION_DELETION_FAILED, ex.getMessage());
        }
    }
}
