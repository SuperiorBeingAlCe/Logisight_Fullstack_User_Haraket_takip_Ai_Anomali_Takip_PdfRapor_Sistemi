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

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // service içinde set edilecek
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "expiredAt", source = "expiredAt")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "ipAddress", source = "ipAddress")
    @Mapping(target = "userAgent", source = "userAgent")
    Session toEntity(CreateSessionDTO dto);

    // DTO → Entity Güncelleme (Null olmayanları update et)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    Session updateEntity(UpdateSessionDTO dto, @MappingTarget Session entity);

    // Entity → DTO
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    SessionResponseDTO toResponseDto(Session entity);
}