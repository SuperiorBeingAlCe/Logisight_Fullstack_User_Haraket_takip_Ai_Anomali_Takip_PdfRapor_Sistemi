package com.Logisight.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;

@Mapper(componentModel = "spring", imports = {java.util.stream.Collectors.class})
public interface AnomalyMapper {

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // servis katmanında setlenecek veya @AfterMapping ile proxy setlenir
    @Mapping(target = "relatedActions", ignore = true)
    @Mapping(target = "anomalyType", source = "anomalyType")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "detectedAt", source = "detectedAt")
    @Mapping(target = "resolved", source = "resolved")
    @Mapping(target = "resolvedAt", source = "resolvedAt")
    Anomaly toEntity(CreateAnomalyDTO dto);

    // Update DTO -> Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "relatedActions", ignore = true)
    @Mapping(target = "anomalyType", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "detectedAt", ignore = true)
    @Mapping(target = "resolved", source = "resolved")
    @Mapping(target = "resolvedAt", source = "resolvedAt")
    Anomaly updateEntity(UpdateAnomalyDTO dto, @MappingTarget Anomaly entity);

    // Entity -> Response DTO
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "relatedUserActionIds",
             expression = "java(entity.getRelatedActions().stream().map(a -> a.getId()).collect(Collectors.toList()))")
    AnomalyResponseDTO toResponseDto(Anomaly entity);

    // opsiyonel: Create sonrası sadece userId verildiğinde id ile User proxy koy
    @AfterMapping
    default void linkUser(@MappingTarget Anomaly entity, CreateAnomalyDTO dto) {
        if (dto.getUserId() != null) {
            User u = new User();
            u.setId(dto.getUserId());
            entity.setUser(u);
        }
    }
}