package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);
    
    @Override
    public NotificationResponseDTO createNotification(CreateNotificationDTO createDTO) {
        // 1️⃣ Alıcı kullanıcıyı bul
        User recipient = userRepo.findById(createDTO.getRecipientId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_RECIPIENT_NOT_FOUND));

        // 2️⃣ Notification entity oluştur
        Notification notification = mapper.toEntity(createDTO);
        notification.setRecipient(recipient);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false); // ⚡ Burayı unutma, DB NOT NULL hatası olmasın

        Notification saved;
        try {
            // 3️⃣ DB kaydet
            saved = notificationRepo.save(notification);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.NOTIFICATION_CREATION_FAILED, ex.getMessage());
        }

        // 4️⃣ WebSocket ile anlık push
        try {
            simpMessagingTemplate.convertAndSend(
                "/topic/notifications/" + recipient.getId(),
                mapper.toResponseDto(saved)
            );
        } catch (Exception e) {
            log.warn("Notification WebSocket gönderilemedi: {}", e.getMessage());
        }

        // 5️⃣ Response DTO döndür
        return mapper.toResponseDto(saved);
    }

    @Override
    @CachePut(value = "notifications", key = "#id")
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
    @Cacheable(value = "notifications", key = "#id")
    public Optional<NotificationResponseDTO> getNotificationById(Long id) {
        return notificationRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "notificationsByRecipient", key = "#userId")
    public List<NotificationResponseDTO> getNotificationsByRecipientId(Long userId) {
        return notificationRepo.findByRecipientId(userId)
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "unreadNotifications", key = "#userId")
    public List<NotificationResponseDTO> getUnreadNotificationsByRecipientId(Long userId) {
        return notificationRepo.findByRecipientIdAndReadFalse(userId)
            .stream()
            .map(mapper::toResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "unreadNotificationCount", key = "#userId")
    public long countUnreadNotificationsByRecipientId(Long userId) {
        return notificationRepo.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    @CacheEvict(value = "notifications", key = "#id")
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
    @CacheEvict(value = "notifications", key = "#id")
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
