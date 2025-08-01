package com.Logisight.service.concretes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;
import com.Logisight.entity.Notification;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.NotificationMapper;
import com.Logisight.repository.NotificationRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationManager implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;
    private final NotificationMapper mapper;

    @Override
    public NotificationResponseDTO createNotification(CreateNotificationDTO createDTO) {
        User recipient = userRepo.findById(createDTO.getRecipientId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_RECIPIENT_NOT_FOUND));

        Notification notification = mapper.toEntity(createDTO);
        notification.setRecipient(recipient);

        Notification saved;
        try {
            saved = notificationRepo.save(notification);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.NOTIFICATION_CREATION_FAILED, ex.getMessage());
        }

        return mapper.toResponseDto(saved);
    }

    @Override
    public Optional<NotificationResponseDTO> updateNotification(Long id, UpdateNotificationDTO updateDTO) {
        Notification notification = notificationRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        mapper.updateEntity(updateDTO, notification);

        Notification updated;
        try {
            updated = notificationRepo.save(notification);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.NOTIFICATION_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    public Optional<NotificationResponseDTO> getNotificationById(Long id) {
        return notificationRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsByRecipientId(Long userId) {
        return notificationRepo.findByRecipientId(userId)
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponseDTO> getUnreadNotificationsByRecipientId(Long userId) {
        return notificationRepo.findByRecipientIdAndReadFalse(userId)
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public long countUnreadNotificationsByRecipientId(Long userId) {
        return notificationRepo.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    public void markNotificationAsRead(Long id) {
        Notification notification = notificationRepo.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.setRead(true);
        try {
            notificationRepo.save(notification);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.NOTIFICATION_UPDATE_FAILED, ex.getMessage());
        }
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        try {
            notificationRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.NOTIFICATION_DELETION_FAILED, ex.getMessage());
        }
    }
}
