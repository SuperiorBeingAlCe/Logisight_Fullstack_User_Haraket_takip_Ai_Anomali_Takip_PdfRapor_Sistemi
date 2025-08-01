package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;
import com.Logisight.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    // Create DTO → Entity
    @Mapping(target = "id", ignore = true)
    Role toEntity(CreateRoleDTO dto);

    // Update DTO → Entity (sadece gelen alanları günceller)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateRoleDTO dto, @MappingTarget Role entity);

    // Entity → Response DTO
    RoleResponseDto toResponseDto(Role role);
}

