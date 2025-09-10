package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;
import com.Logisight.entity.Notification;
import com.Logisight.entity.User;
import com.Logisight.mapper.NotificationMapper;
import com.Logisight.repository.NotificationRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.concretes.NotificationManager;

@ExtendWith(MockitoExtension.class)
public class NotificationManagerTest {
	 @InjectMocks
	    private NotificationManager notificationManager;

	    @Mock
	    private NotificationRepository notificationRepo;

	    @Mock
	    private UserRepository userRepo;

	    @Mock
	    private NotificationMapper mapper;

	    private User user;
	    private Notification notification;
	    private NotificationResponseDTO responseDTO;

	    @BeforeEach
	    void setUp() {
	        user = new User();
	        user.setId(1L);

	        notification = new Notification();
	        notification.setId(100L);
	        notification.setRecipient(user);
	        notification.setRead(false);

	        responseDTO = new NotificationResponseDTO();
	        responseDTO.setId(100L);
	        responseDTO.setRecipientId(user.getId());
	        responseDTO.setRead(false);
	    }

	    @Test
	    void createNotification_success() {
	        CreateNotificationDTO createDTO = new CreateNotificationDTO();
	        createDTO.setRecipientId(user.getId());

	        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
	        when(mapper.toEntity(createDTO)).thenReturn(notification);
	        when(notificationRepo.save(notification)).thenReturn(notification);
	        when(mapper.toResponseDto(notification)).thenReturn(responseDTO);

	        NotificationResponseDTO result = notificationManager.createNotification(createDTO);

	        assertNotNull(result);
	        assertEquals(responseDTO.getId(), result.getId());
	        verify(notificationRepo).save(notification);
	    }

	    @Test
	    void updateNotification_success() {
	        UpdateNotificationDTO updateDTO = new UpdateNotificationDTO();
	        updateDTO.setRead(true);

	        when(notificationRepo.findById(notification.getId())).thenReturn(Optional.of(notification));
	        doAnswer(invocation -> {
	            notification.setRead(updateDTO.getRead());
	            return null;
	        }).when(mapper).updateEntity(updateDTO, notification);

	        when(notificationRepo.save(notification)).thenReturn(notification);
	        when(mapper.toResponseDto(notification)).thenReturn(responseDTO);

	        Optional<NotificationResponseDTO> result = notificationManager.updateNotification(notification.getId(), updateDTO);

	        assertTrue(result.isPresent());
	        verify(notificationRepo).save(notification);
	    }

	    @Test
	    void getNotificationById_found() {
	        when(notificationRepo.findById(notification.getId())).thenReturn(Optional.of(notification));
	        when(mapper.toResponseDto(notification)).thenReturn(responseDTO);

	        Optional<NotificationResponseDTO> result = notificationManager.getNotificationById(notification.getId());

	        assertTrue(result.isPresent());
	        assertEquals(notification.getId(), result.get().getId());
	    }

	    @Test
	    void markNotificationAsRead_success() {
	        when(notificationRepo.findById(notification.getId())).thenReturn(Optional.of(notification));
	        when(notificationRepo.save(notification)).thenReturn(notification);

	        notificationManager.markNotificationAsRead(notification.getId());

	        assertTrue(notification.getRead());
	        verify(notificationRepo).save(notification);
	    }

	    @Test
	    void deleteNotification_success() {
	        when(notificationRepo.existsById(notification.getId())).thenReturn(true);

	        notificationManager.deleteNotification(notification.getId());

	        verify(notificationRepo).deleteById(notification.getId());
	    }
}
