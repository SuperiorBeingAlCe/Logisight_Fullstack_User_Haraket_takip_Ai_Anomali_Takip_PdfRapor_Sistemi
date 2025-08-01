package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;
import com.Logisight.entity.Anomaly;

@Mapper(componentModel = "spring")
public interface AnomalyMapper {

    // Create DTO → Entity
    @Mapping(target = "id", ignore = true)  // kesinlikle kalmalı
    @Mapping(target = "detectedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true) // Serviste manuel setlenecek
    @Mapping(target = "relatedActions", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    Anomaly toEntity(CreateAnomalyDTO dto);

    // Update DTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // opsiyonel ama bırakabilirsin
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "relatedActions", ignore = true)
    Anomaly updateEntity(UpdateAnomalyDTO dto, @MappingTarget Anomaly anomaly);

    // Entity → Response DTO
    @Mapping(target = "userId", source = "user.id")
    AnomalyResponseDTO toResponseDto(Anomaly anomaly);
}
