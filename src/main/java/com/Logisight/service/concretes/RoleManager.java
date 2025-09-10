package com.Logisight.service.concretes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;
import com.Logisight.entity.Role;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.RoleMapper;
import com.Logisight.repository.RoleRepository;
import com.Logisight.service.abstracts.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleManager implements RoleService {

    private final RoleRepository roleRepo;
    private final RoleMapper mapper;

    @Override
    public RoleResponseDto createRole(CreateRoleDTO createDTO) {
        if (roleRepo.existsByName(createDTO.getName())) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        Role entity = mapper.toEntity(createDTO);

        Role saved;
        try {
            saved = roleRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return mapper.toResponseDto(saved);
    }

    @Override
    @CachePut(value = "roles", key = "#id")
    public Optional<RoleResponseDto> updateRole(Long id, UpdateRoleDTO updateDTO) {
        Role entity = roleRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        mapper.updateEntity(updateDTO, entity);

        Role updated;
        try {
            updated = roleRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    @Cacheable(value = "roles", key = "#id")
    public Optional<RoleResponseDto> getRoleById(Long id) {
        return roleRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "rolesByName", key = "#name")
    public Optional<RoleResponseDto> getRoleByName(String name) {
        return roleRepo.findByName(name)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "allRoles")
    public List<RoleResponseDto> getAllRoles() {
        return roleRepo.findAll()
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepo.existsByName(name);
    }

    @Override
    @CacheEvict(value = {"roles", "rolesByName", "allRoles"}, allEntries = true)
    public void deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        try {
            roleRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}

