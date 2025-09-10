package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.response.NotificationResponseDTO;
import com.Logisight.dto.update.UpdateNotificationDTO;
import com.Logisight.entity.Notification;
import com.Logisight.entity.User;
import com.Logisight.mapper.NotificationMapper;

public class NotificationMapperTest {

	private NotificationMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = Mappers.getMapper(NotificationMapper.class);
	}
	 @Test
	    void toEntity_shouldMapCreateDtoToEntity() {
	        CreateNotificationDTO dto = new CreateNotificationDTO();
	        dto.setMessage("Yeni bildirim!");
	        dto.setCreatedAt(LocalDateTime.of(2025, 8, 14, 12, 0));
	        dto.setRead(false);
	        dto.setRecipientId(42L); // mapper ignore ediyor, entity'de null olacak
	        dto.setLink("/notifications/123");

	        Notification entity = mapper.toEntity(dto);

	        assertThat(entity.getId()).isNull(); // DB auto-gen
	        assertThat(entity.getRecipient()).isNull(); // servis içinde setlenecek
	        assertThat(entity.getMessage()).isEqualTo(dto.getMessage());
	        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
	        assertThat(entity.getRead()).isEqualTo(dto.getRead());
	        assertThat(entity.getLink()).isEqualTo(dto.getLink());
	    }

	    @Test
	    void updateEntity_shouldMergeNonNullFields() {
	        Notification existing = new Notification();
	        existing.setId(1L);
	        existing.setMessage("Eski mesaj"); // ignore ediliyor
	        existing.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 0)); // ignore ediliyor
	        existing.setRead(false);
	        existing.setLink("/old/link");
	        existing.setRecipient(null); // ignore ediliyor

	        UpdateNotificationDTO dto = new UpdateNotificationDTO();
	        dto.setRead(true);
	        dto.setLink("/new/link");

	        mapper.updateEntity(dto, existing);

	        assertThat(existing.getId()).isEqualTo(1L); // korunmalı
	        assertThat(existing.getRecipient()).isNull(); // ignore edildi
	        assertThat(existing.getMessage()).isEqualTo("Eski mesaj"); // ignore edildi
	        assertThat(existing.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0)); // ignore edildi
	        assertThat(existing.getRead()).isEqualTo(dto.getRead()); // güncellendi
	        assertThat(existing.getLink()).isEqualTo(dto.getLink()); // güncellendi
	    }

	    @Test
	    void toResponseDto_shouldMapEntityToResponseDto() {
	        User recipient = new User();
	        recipient.setId(42L);
	        recipient.setUsername("example");

	        Notification entity = new Notification();
	        entity.setId(5L);
	        entity.setMessage("Bildirim metni");
	        entity.setCreatedAt(LocalDateTime.of(2025, 8, 14, 14, 0));
	        entity.setRead(true);
	        entity.setRecipient(recipient);
	        entity.setLink("/notifications/5");
 
	        NotificationResponseDTO dto = mapper.toResponseDto(entity);

	        assertThat(dto.getId()).isEqualTo(entity.getId());
	        assertThat(dto.getMessage()).isEqualTo(entity.getMessage());
	        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
	        assertThat(dto.getRead()).isEqualTo(entity.getRead());
	        assertThat(dto.getRecipientId()).isEqualTo(recipient.getId());
	        assertThat(dto.getRecipientUsername()).isEqualTo(recipient.getUsername());
	        assertThat(dto.getLink()).isEqualTo(entity.getLink());
	    }}