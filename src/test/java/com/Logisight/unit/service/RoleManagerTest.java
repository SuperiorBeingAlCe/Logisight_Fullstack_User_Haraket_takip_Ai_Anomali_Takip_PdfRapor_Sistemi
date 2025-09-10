package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
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

import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;
import com.Logisight.entity.Role;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.RoleMapper;
import com.Logisight.repository.RoleRepository;
import com.Logisight.service.concretes.RoleManager;

@ExtendWith(MockitoExtension.class)
public class RoleManagerTest {
	 @InjectMocks
	    private RoleManager roleManager;

	    @Mock
	    private RoleRepository roleRepo;

	    @Mock
	    private RoleMapper mapper;

	    private Role role;
	    private RoleResponseDto responseDto;

	    @BeforeEach
	    void setUp() {
	        role = new Role();
	        role.setId(1L);
	        role.setName("ADMIN");

	        responseDto = new RoleResponseDto();
	        responseDto.setId(role.getId());
	        responseDto.setName(role.getName());
	    }

	    @Test
	    void createRole_success() {
	        CreateRoleDTO createDTO = new CreateRoleDTO();
	        createDTO.setName("ADMIN");

	        when(roleRepo.existsByName(createDTO.getName())).thenReturn(false);
	        when(mapper.toEntity(createDTO)).thenReturn(role);
	        when(roleRepo.save(role)).thenReturn(role);
	        when(mapper.toResponseDto(role)).thenReturn(responseDto);

	        RoleResponseDto result = roleManager.createRole(createDTO);

	        assertNotNull(result);
	        assertEquals("ADMIN", result.getName());
	        verify(roleRepo).save(role);
	    }

	    @Test
	    void createRole_alreadyExists() {
	        CreateRoleDTO createDTO = new CreateRoleDTO();
	        createDTO.setName("ADMIN");

	        when(roleRepo.existsByName("ADMIN")).thenReturn(true);

	        BusinessException ex = assertThrows(BusinessException.class,
	            () -> roleManager.createRole(createDTO));

	        assertEquals(ErrorCode.ROLE_ALREADY_EXISTS, ex.getErrorCode());
	    }

	    @Test
	    void updateRole_success() {
	        UpdateRoleDTO updateDTO = new UpdateRoleDTO();
	        updateDTO.setName("SUPER_ADMIN");

	        when(roleRepo.findById(1L)).thenReturn(Optional.of(role));
	        doAnswer(invocation -> {
	            role.setName(updateDTO.getName());
	            return null;
	        }).when(mapper).updateEntity(updateDTO, role);
	        when(roleRepo.save(role)).thenReturn(role);
	        when(mapper.toResponseDto(role)).thenReturn(responseDto);

	        Optional<RoleResponseDto> result = roleManager.updateRole(1L, updateDTO);

	        assertTrue(result.isPresent());
	        assertEquals("SUPER_ADMIN", role.getName());
	    }

	    @Test
	    void getRoleById_found() {
	        when(roleRepo.findById(1L)).thenReturn(Optional.of(role));
	        when(mapper.toResponseDto(role)).thenReturn(responseDto);

	        Optional<RoleResponseDto> result = roleManager.getRoleById(1L);

	        assertTrue(result.isPresent());
	        assertEquals("ADMIN", result.get().getName());
	    }

	    @Test
	    void getRoleByName_found() {
	        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(role));
	        when(mapper.toResponseDto(role)).thenReturn(responseDto);

	        Optional<RoleResponseDto> result = roleManager.getRoleByName("ADMIN");

	        assertTrue(result.isPresent());
	        assertEquals("ADMIN", result.get().getName());
	    }

	    @Test
	    void getAllRoles_success() {
	        when(roleRepo.findAll()).thenReturn(List.of(role));
	        when(mapper.toResponseDto(role)).thenReturn(responseDto);

	        List<RoleResponseDto> result = roleManager.getAllRoles();

	        assertEquals(1, result.size());
	        assertEquals("ADMIN", result.get(0).getName());
	    }

	    @Test
	    void deleteRole_success() {
	        when(roleRepo.existsById(1L)).thenReturn(true);

	        roleManager.deleteRole(1L);

	        verify(roleRepo).deleteById(1L);
	    }

	    @Test
	    void deleteRole_notFound() {
	        when(roleRepo.existsById(2L)).thenReturn(false);

	        BusinessException ex = assertThrows(BusinessException.class,
	            () -> roleManager.deleteRole(2L));

	        assertEquals(ErrorCode.ROLE_NOT_FOUND, ex.getErrorCode());
	    }
}
