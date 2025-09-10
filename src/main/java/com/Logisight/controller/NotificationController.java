package com.Logisight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.aspect.CapturePhase;
import com.Logisight.aspect.LogUserAction;
import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;
import com.Logisight.entity.Notification;
import com.Logisight.service.abstracts.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class NotificationController {
	private final NotificationService notificationService;

    @PostMapping
    @LogUserAction(
        actionType = "NOTIFICATION_CREATE",
        dynamicDetail = true,
        entityClass = Notification.class,
        phase = CapturePhase.AFTER,
        lookupField = "id"
    )
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody @Valid CreateNotificationDTO createDTO) {
        NotificationResponseDTO response = notificationService.createNotification(createDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @LogUserAction(
        actionType = "NOTIFICATION_UPDATE",
        dynamicDetail = true,
        entityClass = Notification.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<NotificationResponseDTO> updateNotification(
            @PathVariable Long id,
            @RequestBody @Valid UpdateNotificationDTO updateDTO) {
        return notificationService.updateNotification(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-recipient/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByRecipientId(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByRecipientId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/by-recipient/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotificationsByRecipientId(@PathVariable Long userId) {
        List<NotificationResponseDTO> unreadNotifications = notificationService.getUnreadNotificationsByRecipientId(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    @GetMapping("/unread/count/by-recipient/{userId}")
    public ResponseEntity<Long> countUnreadNotificationsByRecipientId(@PathVariable Long userId) {
        long count = notificationService.countUnreadNotificationsByRecipientId(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/mark-as-read")
    @LogUserAction(
        actionType = "NOTIFICATION_MARK_READ",
        entityClass = Notification.class,
        phase = CapturePhase.AFTER,
        lookupField = "id"
    )
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @LogUserAction(
        actionType = "NOTIFICATION_DELETE",
        entityClass = Notification.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}