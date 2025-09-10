package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.AnomalyMapper;
import com.Logisight.repository.AnomalyRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.concretes.AnomalyManager;

public class AnomalyManagerTest {
	
	 @InjectMocks
	    private AnomalyManager anomalyManager;

	    @Mock
	    private AnomalyRepository anomalyRepo;

	    @Mock
	    private UserRepository userRepo;

	    @Mock
	    private AnomalyMapper mapper;

	    private User testUser;
	    private Anomaly testAnomaly;
	    private AnomalyResponseDTO testResponseDto;

	    @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);

	        testUser = new User();
	        testUser.setId(1L);
	        testUser.setUsername("Test User");

	        testAnomaly = new Anomaly();
	        testAnomaly.setId(1L);
	        testAnomaly.setUser(testUser);
	        testAnomaly.setResolved(false);

	        testResponseDto = new AnomalyResponseDTO();
	        testResponseDto.setId(1L);
	        testResponseDto.setUserId(1L);
	    }

	    @Test
	    void createAnomaly_success() {
	        CreateAnomalyDTO dto = new CreateAnomalyDTO();
	        dto.setUserId(1L);

	        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
	        when(mapper.toEntity(dto)).thenReturn(testAnomaly);
	        when(anomalyRepo.save(testAnomaly)).thenReturn(testAnomaly);
	        when(mapper.toResponseDto(testAnomaly)).thenReturn(testResponseDto);

	        AnomalyResponseDTO result = anomalyManager.createAnomaly(dto);

	        assertNotNull(result);
	        assertEquals(1L, result.getId());
	        verify(anomalyRepo, times(1)).save(testAnomaly);
	    }

	    @Test
	    void createAnomaly_userNotFound() {
	        CreateAnomalyDTO dto = new CreateAnomalyDTO();
	        dto.setUserId(2L);

	        when(userRepo.findById(2L)).thenReturn(Optional.empty());

	        BusinessException exception = assertThrows(BusinessException.class, () -> anomalyManager.createAnomaly(dto));
	        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	    }

	    @Test
	    void getAnomalyById_found() {
	        when(anomalyRepo.findById(1L)).thenReturn(Optional.of(testAnomaly));
	        when(mapper.toResponseDto(testAnomaly)).thenReturn(testResponseDto);

	        Optional<AnomalyResponseDTO> result = anomalyManager.getAnomalyById(1L);

	        assertTrue(result.isPresent());
	        assertEquals(1L, result.get().getId());
	    }

	    @Test
	    void markAnomalyAsResolved_success() {
	        when(anomalyRepo.findById(1L)).thenReturn(Optional.of(testAnomaly));
	        when(anomalyRepo.save(testAnomaly)).thenReturn(testAnomaly);

	        anomalyManager.markAnomalyAsResolved(1L);

	        assertTrue(testAnomaly.isResolved());
	        verify(anomalyRepo, times(1)).save(testAnomaly);
	    }

	    @Test
	    void deleteAnomaly_success() {
	        when(anomalyRepo.existsById(1L)).thenReturn(true);
	        doNothing().when(anomalyRepo).deleteById(1L);

	        anomalyManager.deleteAnomaly(1L);

	        verify(anomalyRepo, times(1)).deleteById(1L);
	    }
}
