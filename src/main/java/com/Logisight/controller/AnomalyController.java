package com.Logisight.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.aspect.CapturePhase;
import com.Logisight.aspect.LogUserAction;
import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.service.abstracts.AnomalyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AnomalyController {
	 private final AnomalyService anomalyService;

	    @PostMapping
	    @LogUserAction(
	        actionType = "ANOMALY_CREATE",
	        dynamicDetail = true,
	        entityClass = Anomaly.class,
	        phase = CapturePhase.AFTER,
	        lookupField = "id"
	    )
	    public ResponseEntity<AnomalyResponseDTO> createAnomaly(@RequestBody @Valid CreateAnomalyDTO createDTO) {
	        AnomalyResponseDTO response = anomalyService.createAnomaly(createDTO);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }

	    @PutMapping("/{id}")
	    @LogUserAction(
	        actionType = "ANOMALY_UPDATE",
	        dynamicDetail = true,
	        entityClass = Anomaly.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<AnomalyResponseDTO> updateAnomaly(
	            @PathVariable Long id,
	            @RequestBody @Valid UpdateAnomalyDTO updateDTO) {
	        return anomalyService.updateAnomaly(id, updateDTO)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<AnomalyResponseDTO> getAnomalyById(@PathVariable Long id) {
	        return anomalyService.getAnomalyById(id)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/by-user/{userId}")
	    public ResponseEntity<Page<AnomalyResponseDTO>> getAnomaliesByUserId(
	            @PathVariable Long userId,
	            Pageable pageable) {
	        Page<AnomalyResponseDTO> page = anomalyService.getAnomaliesByUserId(userId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/unresolved")
	    public ResponseEntity<Page<AnomalyResponseDTO>> getUnresolvedAnomalies(Pageable pageable) {
	        Page<AnomalyResponseDTO> page = anomalyService.getUnresolvedAnomalies(pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/by-type")
	    public ResponseEntity<Page<AnomalyResponseDTO>> getAnomaliesByType(
	            @RequestParam String anomalyType,
	            Pageable pageable) {
	        Page<AnomalyResponseDTO> page = anomalyService.getAnomaliesByType(anomalyType, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/unresolved/by-user/{userId}")
	    public ResponseEntity<Page<AnomalyResponseDTO>> getUnresolvedAnomaliesByUserId(
	            @PathVariable Long userId,
	            Pageable pageable) {
	        Page<AnomalyResponseDTO> page = anomalyService.getUnresolvedAnomaliesByUserId(userId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @PostMapping("/{id}/mark-as-resolved")
	    @LogUserAction(
	        actionType = "ANOMALY_MARK_RESOLVED",
	        entityClass = Anomaly.class,
	        phase = CapturePhase.AFTER,
	        lookupField = "id"
	    )
	    public ResponseEntity<Void> markAnomalyAsResolved(@PathVariable Long id) {
	        anomalyService.markAnomalyAsResolved(id);
	        return ResponseEntity.noContent().build();
	    }

	    @DeleteMapping("/{id}")
	    @LogUserAction(
	        actionType = "ANOMALY_DELETE",
	        entityClass = Anomaly.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<Void> deleteAnomaly(@PathVariable Long id) {
	        anomalyService.deleteAnomaly(id);
	        return ResponseEntity.noContent().build();
	    }
	}
