package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "enabled", constant = "true") // varsayılan olarak aktif
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "roles", ignore = true) // servis içinde atanmalı
    @Mapping(target = "actions", ignore = true)
    User toEntity(UserCreateDto dto);

    // Update DTO -> Entity güncelleme
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "actions", ignore = true)
    User updateUserFromDto(UpdateUserDTO dto, @MappingTarget User user);

    // Entity -> Response DTO
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()))")
    UserResponseDto toResponseDto(User user);
    
}