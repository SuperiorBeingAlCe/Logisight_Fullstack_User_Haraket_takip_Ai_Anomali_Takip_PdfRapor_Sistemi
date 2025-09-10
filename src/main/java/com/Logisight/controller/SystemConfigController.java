package com.Logisight.controller;

import java.util.List;

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
import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;
import com.Logisight.entity.SystemConfig;
import com.Logisight.service.abstracts.SystemConfigService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system-configs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @PostMapping
    @LogUserAction(
        actionType = "SYSTEM_CONFIG_CREATE",
        dynamicDetail = true,
        entityClass = SystemConfig.class,
        phase = CapturePhase.AFTER,
        lookupField = "id"
    )
    public ResponseEntity<SystemConfigResponseDTO> createSystemConfig(
            @RequestBody @Valid CreateSystemConfigDTO createDTO) {
        SystemConfigResponseDTO response = systemConfigService.createSystemConfig(createDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @LogUserAction(
        actionType = "SYSTEM_CONFIG_UPDATE",
        dynamicDetail = true,
        entityClass = SystemConfig.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<SystemConfigResponseDTO> updateSystemConfig(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSystemConfigDTO updateDTO) {
        return systemConfigService.updateSystemConfig(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemConfigResponseDTO> getSystemConfigById(@PathVariable Long id) {
        return systemConfigService.getSystemConfigById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-key")
    public ResponseEntity<SystemConfigResponseDTO> getSystemConfigByKey(@RequestParam String key) {
        return systemConfigService.getSystemConfigByKey(key)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SystemConfigResponseDTO>> getAllSystemConfigs() {
        List<SystemConfigResponseDTO> configs = systemConfigService.getAllSystemConfigs();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByKey(@RequestParam String key) {
        boolean exists = systemConfigService.existsByKey(key);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{id}")
    @LogUserAction(
        actionType = "SYSTEM_CONFIG_DELETE",
        entityClass = SystemConfig.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<Void> deleteSystemConfig(@PathVariable Long id) {
        systemConfigService.deleteSystemConfig(id);
        return ResponseEntity.noContent().build();
    }
}