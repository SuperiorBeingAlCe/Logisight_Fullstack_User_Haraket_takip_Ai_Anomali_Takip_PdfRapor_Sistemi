package com.Logisight.service.concretes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;
import com.Logisight.entity.SystemConfig;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.SystemConfigMapper;
import com.Logisight.repository.SystemConfigRepository;
import com.Logisight.service.abstracts.SystemConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemConfigManager implements SystemConfigService {

    private final SystemConfigRepository configRepo;
    private final SystemConfigMapper mapper;

    @Override
    @CachePut(value = "systemConfigs", key = "#createDTO.key")
    public SystemConfigResponseDTO createSystemConfig(CreateSystemConfigDTO createDTO) {
        if (configRepo.existsByKey(createDTO.getKey())) {
            throw new BusinessException(ErrorCode.CONFIG_KEY_ALREADY_EXISTS);
        }
        SystemConfig entity = mapper.toEntity(createDTO);
        SystemConfig saved;
        try {
            saved = configRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return mapper.toResponseDto(saved);
    }

    @Override
    @CachePut(value = "systemConfigs", key = "#result.get().key")
    public Optional<SystemConfigResponseDTO> updateSystemConfig(Long id, UpdateSystemConfigDTO updateDTO) {
        SystemConfig entity = configRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CONFIG_KEY_NOT_FOUND));

        mapper.updateEntity(updateDTO, entity);

        SystemConfig updated;
        try {
            updated = configRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    @Cacheable(value = "systemConfigs", key = "#id")
    public Optional<SystemConfigResponseDTO> getSystemConfigById(Long id) {
        return configRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "systemConfigs", key = "#key")
    public Optional<SystemConfigResponseDTO> getSystemConfigByKey(String key) {
        return configRepo.findByKey(key)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "systemConfigsAll")
    public List<SystemConfigResponseDTO> getAllSystemConfigs() {
        return configRepo.findAll()
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByKey(String key) {
        return configRepo.existsByKey(key); // bunu cache'e alma, çünkü doğruluk her şeydir.
    }

    @Override
    @CacheEvict(value = {"systemConfigs", "systemConfigsAll"}, allEntries = true)
    public void deleteSystemConfig(Long id) {
        if (!configRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.CONFIG_KEY_NOT_FOUND);
        }
        try {
            configRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}