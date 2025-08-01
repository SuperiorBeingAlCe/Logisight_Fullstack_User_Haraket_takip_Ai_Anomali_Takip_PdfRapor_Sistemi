package com.Logisight.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;

public interface NotificationService {
	 NotificationResponseDTO createNotification(CreateNotificationDTO createDTO);

	    Optional<NotificationResponseDTO> updateNotification(Long id, UpdateNotificationDTO updateDTO);

	    Optional<NotificationResponseDTO> getNotificationById(Long id);

	    List<NotificationResponseDTO> getNotificationsByRecipientId(Long userId);

	    List<NotificationResponseDTO> getUnreadNotificationsByRecipientId(Long userId);

	    long countUnreadNotificationsByRecipientId(Long userId);

	    void markNotificationAsRead(Long id);

	    void deleteNotification(Long id);
	}