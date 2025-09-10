package com.Logisight.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;

public interface RoleService {
	RoleResponseDto createRole(CreateRoleDTO createDTO);

    Optional<RoleResponseDto> updateRole(Long id, UpdateRoleDTO updateDTO);

    Optional<RoleResponseDto> getRoleById(Long id);

    Optional<RoleResponseDto> getRoleByName(String name);

    List<RoleResponseDto> getAllRoles();

    boolean existsByName(String name);

    void deleteRole(Long id);

}
