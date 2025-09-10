package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;
import com.Logisight.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // Create DTO -> Entity
    @Mapping(target = "id", ignore = true) // DB auto-gen
    @Mapping(target = "recipient", ignore = true) // servis içinde setlenecek
    @Mapping(target = "message", source = "message")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "read", source = "read")
    @Mapping(target = "link", source = "link")
    Notification toEntity(CreateNotificationDTO dto);

    // Update DTO -> Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "read", source = "read")
    @Mapping(target = "link", source = "link")
    Notification updateEntity(UpdateNotificationDTO dto, @MappingTarget Notification entity);

    // Entity -> Response DTO
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "recipientUsername", source = "recipient.username")
    NotificationResponseDTO toResponseDto(Notification entity);
}