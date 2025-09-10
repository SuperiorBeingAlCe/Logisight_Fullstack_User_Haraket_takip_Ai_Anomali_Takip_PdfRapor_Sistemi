package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.entity.UserAction;

@Mapper(componentModel = "spring")
public interface UserActionMapper {

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Servis içinde set edilecek
    @Mapping(target = "anomaly", ignore = true) // Servis içinde set edilecek
    UserAction toEntity(CreateUserActionDTO dto);

    // Update DTO -> Entity (merge)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "anomaly", ignore = true)
    UserAction updateEntity(UpdateUserActionDTO dto, @MappingTarget UserAction entity);

    // Entity -> Response DTO
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "anomalyId", source = "anomaly.id")
    @Mapping(target = "anomalyType", source = "anomaly.anomalyType")
    UserActionResponseDto toResponseDto(UserAction action);
}