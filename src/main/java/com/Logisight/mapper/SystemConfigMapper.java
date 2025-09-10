package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;
import com.Logisight.entity.SystemConfig;

@Mapper(componentModel = "spring")
public interface SystemConfigMapper {

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true)
    SystemConfig toEntity(CreateSystemConfigDTO dto);

    // Update DTO -> Entity (merge)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "key", ignore = true) // KEY değiştirilemez
    SystemConfig updateEntity(UpdateSystemConfigDTO dto, @MappingTarget SystemConfig entity);

    // Entity -> Response DTO
    SystemConfigResponseDTO toResponseDto(SystemConfig entity);
}