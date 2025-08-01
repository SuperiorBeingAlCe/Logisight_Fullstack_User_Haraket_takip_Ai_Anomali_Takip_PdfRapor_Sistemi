package com.Logisight.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;

public interface AnomalyService {
	
	 AnomalyResponseDTO createAnomaly(CreateAnomalyDTO createDTO);

	    Optional<AnomalyResponseDTO> updateAnomaly(Long id, UpdateAnomalyDTO updateDTO);

	    Optional<AnomalyResponseDTO> getAnomalyById(Long id);

	    List<AnomalyResponseDTO> getAnomaliesByUserId(Long userId);

	    List<AnomalyResponseDTO> getUnresolvedAnomalies();

	    List<AnomalyResponseDTO> getAnomaliesByType(String anomalyType);

	    List<AnomalyResponseDTO> getUnresolvedAnomaliesByUserId(Long userId);

	    void markAnomalyAsResolved(Long id);

	    void deleteAnomaly(Long id);
}
