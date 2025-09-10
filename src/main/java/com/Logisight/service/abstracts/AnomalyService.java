package com.Logisight.service.abstracts;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;

public interface AnomalyService {
	
	 AnomalyResponseDTO createAnomaly(CreateAnomalyDTO createDTO);

	    Optional<AnomalyResponseDTO> updateAnomaly(Long id, UpdateAnomalyDTO updateDTO);

	    Optional<AnomalyResponseDTO> getAnomalyById(Long id);

	    Page<AnomalyResponseDTO> getAnomaliesByUserId(Long userId, Pageable pageable);

	    Page<AnomalyResponseDTO> getUnresolvedAnomalies(Pageable pageable);

	    Page<AnomalyResponseDTO> getAnomaliesByType(String anomalyType, Pageable pageable);

	    Page<AnomalyResponseDTO> getUnresolvedAnomaliesByUserId(Long userId, Pageable pageable);

	    void markAnomalyAsResolved(Long id);

	    void deleteAnomaly(Long id);
}
