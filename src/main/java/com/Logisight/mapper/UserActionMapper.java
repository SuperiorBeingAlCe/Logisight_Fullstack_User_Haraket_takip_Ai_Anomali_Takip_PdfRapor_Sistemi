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
    @Mapping(target = "id", ignore = true)  // Her zaman ignore et
    @Mapping(target = "user", ignore = true)  // Manuel set edilecek
    @Mapping(target = "anomaly", ignore = true)  // Manuel set edilecek
    UserAction toEntity(CreateUserActionDTO dto);

    // Update DTO -> Entity (sadece gelen alanlar update edilir)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "anomaly", ignore = true)
    void updateEntity(UpdateUserActionDTO dto, @MappingTarget UserAction entity);

    // Entity -> Response DTO
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "anomalyId", source = "anomaly.id")
    UserActionResponseDto toResponseDto(UserAction entity);
}
