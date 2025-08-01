package com.Logisight.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.Role;
import com.Logisight.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "actions", ignore = true)
    User toEntity(UserCreateDto dto);

    // UPDATE → sadece DTO'da gelen alanları güncelle
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateUserFromDto(UpdateUserDTO dto, @MappingTarget User user);

    // ENTITY → DTO
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponseDto toResponseDto(User user);

    // Helper method (roles → string)
    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
    }
    }
