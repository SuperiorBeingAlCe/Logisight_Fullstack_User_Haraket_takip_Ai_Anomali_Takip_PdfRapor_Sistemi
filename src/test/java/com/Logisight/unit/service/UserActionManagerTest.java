package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.entity.UserAction;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.UserActionMapper;
import com.Logisight.repository.AnomalyRepository;
import com.Logisight.repository.UserActionRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.concretes.UserActionManager;

@ExtendWith(MockitoExtension.class)
public class UserActionManagerTest {
	 @InjectMocks
	    private UserActionManager actionManager;

	    @Mock
	    private UserActionRepository actionRepo;

	    @Mock
	    private UserRepository userRepo;

	    @Mock
	    private AnomalyRepository anomalyRepo;

	    @Mock
	    private UserActionMapper mapper;

	    private UserAction entity;
	    private UserActionResponseDto responseDto;
	    private User user;

	    @BeforeEach
	    void setUp() {
	        user = new User();
	        user.setId(1L);

	        entity = new UserAction();
	        entity.setId(1L);
	        entity.setUser(user);
	        entity.setActionType("LOGIN");

	        responseDto = new UserActionResponseDto();
	        responseDto.setId(entity.getId());
	        responseDto.setUserId(user.getId());
	        responseDto.setActionType("LOGIN");
	    }

	    @Test
	    void createUserAction_success_withoutAnomaly() {
	        CreateUserActionDTO dto = new CreateUserActionDTO();
	        dto.setUserId(user.getId());
	        dto.setActionType("LOGIN");

	        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
	        when(mapper.toEntity(dto)).thenReturn(entity);
	        when(actionRepo.save(entity)).thenReturn(entity);
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        UserActionResponseDto result = actionManager.createUserAction(dto);

	        assertNotNull(result);
	        assertEquals("LOGIN", result.getActionType());
	        verify(actionRepo, times(1)).save(entity);
	    }

	    @Test
	    void createUserAction_userNotFound() {
	        CreateUserActionDTO dto = new CreateUserActionDTO();
	        dto.setUserId(1L);

	        when(userRepo.findById(1L)).thenReturn(Optional.empty());

	        BusinessException ex = assertThrows(BusinessException.class, () -> actionManager.createUserAction(dto));
	        assertEquals(ErrorCode.USER_ACTION_ASSOCIATED_USER_NOT_FOUND, ex.getErrorCode());
	    }

	    @Test
	    void createUserAction_withAnomaly() {
	        CreateUserActionDTO dto = new CreateUserActionDTO();
	        dto.setUserId(user.getId());
	        dto.setAnomalyId(100L);

	        Anomaly anomaly = new Anomaly();
	        anomaly.setId(100L);

	        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
	        when(anomalyRepo.findById(100L)).thenReturn(Optional.of(anomaly));
	        when(mapper.toEntity(dto)).thenReturn(entity);
	        when(actionRepo.save(entity)).thenReturn(entity);
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        UserActionResponseDto result = actionManager.createUserAction(dto);

	        assertNotNull(result);
	        verify(actionRepo, times(1)).save(entity);
	    }

	    @Test
	    void updateUserAction_success() {
	        UpdateUserActionDTO dto = new UpdateUserActionDTO();
	        dto.setActionType("LOGOUT");

	        when(actionRepo.findById(1L)).thenReturn(Optional.of(entity));
	        doAnswer(invocation -> {
	            entity.setActionType(dto.getActionType());
	            return null;
	        }).when(mapper).updateEntity(dto, entity);
	        when(actionRepo.save(entity)).thenReturn(entity);
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        Optional<UserActionResponseDto> result = actionManager.updateUserAction(1L, dto);

	        assertTrue(result.isPresent());
	        assertEquals("LOGIN", result.get().getActionType()); // mapper mock simülasyonu
	    }

	    @Test
	    void getUserActionById_found() {
	        when(actionRepo.findById(1L)).thenReturn(Optional.of(entity));
	        when(mapper.toResponseDto(entity)).thenReturn(responseDto);

	        Optional<UserActionResponseDto> result = actionManager.getUserActionById(1L);

	        assertTrue(result.isPresent());
	        assertEquals(1L, result.get().getUserId());
	    }

	    @Test
	    void deleteUserAction_success() {
	        when(actionRepo.existsById(1L)).thenReturn(true);

	        actionManager.deleteUserAction(1L);

	        verify(actionRepo, times(1)).deleteById(1L);
	    }

	    @Test
	    void deleteUserAction_notFound() {
	        when(actionRepo.existsById(2L)).thenReturn(false);

	        BusinessException ex = assertThrows(BusinessException.class, () -> actionManager.deleteUserAction(2L));

	        assertEquals(ErrorCode.USER_ACTION_NOT_FOUND, ex.getErrorCode());
	    }
}
