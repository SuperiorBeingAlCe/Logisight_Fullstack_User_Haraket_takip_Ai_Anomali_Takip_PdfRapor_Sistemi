package com.Logisight.service.concretes;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.AnomalyMapper;
import com.Logisight.repository.AnomalyRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.AnomalyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnomalyManager implements AnomalyService {

    private final AnomalyRepository anomalyRepo;
    private final UserRepository userRepo;
    private final AnomalyMapper mapper;

    @Override
    public AnomalyResponseDTO createAnomaly(CreateAnomalyDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Anomaly anomaly = mapper.toEntity(dto);
        anomaly.setUser(user);

        try {
            Anomaly saved = anomalyRepo.save(anomaly);
            return mapper.toResponseDto(saved);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ANOMALY_CREATION_FAILED, e.getMessage());
        }
    }

    @Override
    @CachePut(value = "anomalies", key = "#id")
    public Optional<AnomalyResponseDTO> updateAnomaly(Long id, UpdateAnomalyDTO dto) {
        Anomaly anomaly = anomalyRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANOMALY_NOT_FOUND));

        mapper.updateEntity(dto, anomaly);

        try {
            Anomaly updated = anomalyRepo.save(anomaly);
            return Optional.of(mapper.toResponseDto(updated));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ANOMALY_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "anomalies", key = "#id")
    public Optional<AnomalyResponseDTO> getAnomalyById(Long id) {
        return anomalyRepo.findById(id)
                .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "userAnomalies", key = "#userId")
    public Page<AnomalyResponseDTO> getAnomaliesByUserId(Long userId, Pageable pageable) {
        return anomalyRepo.findByUserId(userId, pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "unresolvedAnomalies")
    public Page<AnomalyResponseDTO> getUnresolvedAnomalies(Pageable pageable) {
        return anomalyRepo.findByResolvedFalse(pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "anomaliesByType", key = "#anomalyType")
    public Page<AnomalyResponseDTO> getAnomaliesByType(String anomalyType, Pageable pageable) {
        return anomalyRepo.findByAnomalyType(anomalyType, pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "unresolvedUserAnomalies", key = "#userId")
    public Page<AnomalyResponseDTO> getUnresolvedAnomaliesByUserId(Long userId, Pageable pageable) {
        return anomalyRepo.findByUserIdAndResolvedFalse(userId, pageable)
                .map(mapper::toResponseDto);
    }

    @Override
    @CacheEvict(value = "anomalies", key = "#id")
    public void markAnomalyAsResolved(Long id) {
        Anomaly anomaly = anomalyRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANOMALY_NOT_FOUND));

        anomaly.setResolved(true);

        try {
            anomalyRepo.save(anomaly);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ANOMALY_UPDATE_FAILED, e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "anomalies", key = "#id")
    public void deleteAnomaly(Long id) {
        if (!anomalyRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.ANOMALY_NOT_FOUND);
        }

        try {
            anomalyRepo.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ANOMALY_DELETION_FAILED, e.getMessage());
        }
    }
}
