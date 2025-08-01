package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.entity.Session;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    // Create DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // servis içinde manuel setlenecek
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Session toEntity(CreateSessionDTO dto);

    // Update DTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Session updateEntity(UpdateSessionDTO dto, @MappingTarget Session entity);

    // Entity → Response DTO
    @Mapping(target = "userId", source = "user.id")
    SessionResponseDTO toResponseDto(Session entity);
}