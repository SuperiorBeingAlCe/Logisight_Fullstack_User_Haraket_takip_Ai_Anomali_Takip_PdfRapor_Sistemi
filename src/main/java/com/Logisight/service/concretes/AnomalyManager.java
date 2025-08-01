package com.Logisight.service.concretes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<AnomalyResponseDTO> getAnomalyById(Long id) {
        return anomalyRepo.findById(id)
                .map(mapper::toResponseDto);
    }

    @Override
    public List<AnomalyResponseDTO> getAnomaliesByUserId(Long userId) {
        return anomalyRepo.findByUserId(userId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyResponseDTO> getUnresolvedAnomalies() {
        return anomalyRepo.findByResolvedFalse().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyResponseDTO> getAnomaliesByType(String anomalyType) {
        return anomalyRepo.findByAnomalyType(anomalyType).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyResponseDTO> getUnresolvedAnomaliesByUserId(Long userId) {
        return anomalyRepo.findByUserIdAndResolvedFalse(userId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
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
