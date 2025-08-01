package com.Logisight.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;

public interface SystemConfigService {
	 SystemConfigResponseDTO createSystemConfig(CreateSystemConfigDTO createDTO);

	    Optional<SystemConfigResponseDTO> updateSystemConfig(Long id, UpdateSystemConfigDTO updateDTO);

	    Optional<SystemConfigResponseDTO> getSystemConfigById(Long id);

	    Optional<SystemConfigResponseDTO> getSystemConfigByKey(String key);

	    List<SystemConfigResponseDTO> getAllSystemConfigs();

	    boolean existsByKey(String key);

	    void deleteSystemConfig(Long id);

	}
