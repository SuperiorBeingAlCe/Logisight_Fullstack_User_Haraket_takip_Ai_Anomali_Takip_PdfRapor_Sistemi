package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;
import com.Logisight.entity.SystemConfig;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.SystemConfigMapper;
import com.Logisight.repository.SystemConfigRepository;
import com.Logisight.service.concretes.SystemConfigManager;

@ExtendWith(MockitoExtension.class)
public class SystemConfigManagerTest {
	 @InjectMocks
	    private SystemConfigManager configManager;

	    @Mock
	    private SystemConfigRepository configRepo;

	    @Mock
	    private SystemConfigMapper mapper;

	    private SystemConfig entity;
	    private SystemConfigResponseDTO responseDto;

	    @BeforeEach
	    void setUp() {
	        entity = new SystemConfig();
	        entity.setId(1L);
	        entity.setKey("configKey");
	        entity.setValue("configValue");

	        responseDto = new SystemConfigResponseDTO();
	        responseDto.setId(entity.getId());
	        responseDto.setKey(entity.getKey());
	        responseDto.setValue(entity.getValue());
	    }

	    @Test
	    void createSystemConfig_success() {
	        CreateSystemConfigDTO createDTO = new CreateSystemConfigDTO();
	        createDTO.setKey("configKey");
	        createDTO.setValue("configValue");

	        when(configRepo.existsByKey(createDTO.getKey())).thenReturn(false);
	        when(mapper.toEntity(createDTO)).thenReturn(entity);
	        when(configRepo.save(entity)).thenReturn(entity);
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        SystemConfigResponseDTO result = configManager.createSystemConfig(createDTO);

	        assertNotNull(result);
	        assertEquals("configKey", result.getKey());
	        verify(configRepo, times(1)).save(entity);
	    }

	    @Test
	    void createSystemConfig_keyAlreadyExists() {
	        CreateSystemConfigDTO createDTO = new CreateSystemConfigDTO();
	        createDTO.setKey("configKey");

	        when(configRepo.existsByKey(createDTO.getKey())).thenReturn(true);

	        BusinessException ex = assertThrows(BusinessException.class, () -> 
	            configManager.createSystemConfig(createDTO));

	        assertEquals(ErrorCode.CONFIG_KEY_ALREADY_EXISTS, ex.getErrorCode());
	    }

	    @Test
	    void updateSystemConfig_success() {
	        UpdateSystemConfigDTO updateDTO = new UpdateSystemConfigDTO();
	        updateDTO.setValue("newValue");

	        when(configRepo.findById(1L)).thenReturn(Optional.of(entity));
	        doAnswer(invocation -> {
	            entity.setValue(updateDTO.getValue());
	            return null;
	        }).when(mapper).updateEntity(updateDTO, entity);
	        when(configRepo.save(entity)).thenReturn(entity);
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        Optional<SystemConfigResponseDTO> result = configManager.updateSystemConfig(1L, updateDTO);

	        assertTrue(result.isPresent());
	        assertEquals("configValue", result.get().getValue());
	    }

	    @Test
	    void updateSystemConfig_notFound() {
	        UpdateSystemConfigDTO updateDTO = new UpdateSystemConfigDTO();

	        when(configRepo.findById(2L)).thenReturn(Optional.empty());

	        BusinessException ex = assertThrows(BusinessException.class,
	            () -> configManager.updateSystemConfig(2L, updateDTO));

	        assertEquals(ErrorCode.CONFIG_KEY_NOT_FOUND, ex.getErrorCode());
	    }

	    @Test
	    void getSystemConfigById_found() {
	        when(configRepo.findById(1L)).thenReturn(Optional.of(entity));
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        Optional<SystemConfigResponseDTO> result = configManager.getSystemConfigById(1L);

	        assertTrue(result.isPresent());
	        assertEquals("configKey", result.get().getKey());
	    }

	    @Test
	    void getSystemConfigByKey_found() {
	        when(configRepo.findByKey("configKey")).thenReturn(Optional.of(entity));
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        Optional<SystemConfigResponseDTO> result = configManager.getSystemConfigByKey("configKey");

	        assertTrue(result.isPresent());
	        assertEquals("configKey", result.get().getKey());
	    }

	    @Test
	    void getAllSystemConfigs() {
	        when(configRepo.findAll()).thenReturn(List.of(entity));
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        List<SystemConfigResponseDTO> result = configManager.getAllSystemConfigs();

	        assertEquals(1, result.size());
	        assertEquals("configKey", result.get(0).getKey());
	    }

	    @Test
	    void existsByKey_true() {
	        when(configRepo.existsByKey("configKey")).thenReturn(true);
	        assertTrue(configManager.existsByKey("configKey"));
	    }

	    @Test
	    void deleteSystemConfig_success() {
	        when(configRepo.existsById(1L)).thenReturn(true);

	        configManager.deleteSystemConfig(1L);

	        verify(configRepo, times(1)).deleteById(1L);
	    }

	    @Test
	    void deleteSystemConfig_notFound() {
	        when(configRepo.existsById(2L)).thenReturn(false);

	        BusinessException ex = assertThrows(BusinessException.class, () -> 
	            configManager.deleteSystemConfig(2L));

	        assertEquals(ErrorCode.CONFIG_KEY_NOT_FOUND, ex.getErrorCode());
	    }
}
