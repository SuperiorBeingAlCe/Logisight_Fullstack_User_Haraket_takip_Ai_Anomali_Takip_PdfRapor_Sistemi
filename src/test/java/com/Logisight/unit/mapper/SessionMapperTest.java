package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.entity.Session;
import com.Logisight.entity.User;
import com.Logisight.mapper.SessionMapper;

public class SessionMapperTest {

	private SessionMapper sessionMapper;
	
	@BeforeEach
	void setup() {
		sessionMapper = Mappers.getMapper(SessionMapper.class);
	}
	
	@Test
	void toEntity_shouldMapCreateDtoToEntity() {
	    CreateSessionDTO dto = new CreateSessionDTO();
	    dto.setSessionId("sess123");
	    dto.setCreatedAt(LocalDateTime.of(2025,8,14,12,0));
	    dto.setExpiredAt(LocalDateTime.of(2025,8,14,14,0));
	    dto.setActive(true);
	    dto.setIpAddress("192.168.1.1");
	    dto.setUserAgent("Mozilla/5.0");

	    Session entity = sessionMapper.toEntity(dto);

	    assertThat(entity.getId()).isNull();
	    assertThat(entity.getUser()).isNull();
	    assertThat(entity.getSessionId()).isEqualTo(dto.getSessionId());
	    assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
	    assertThat(entity.getExpiredAt()).isEqualTo(dto.getExpiredAt());
	    assertThat(entity.isActive()).isEqualTo(dto.isActive());
	    assertThat(entity.getIpAddress()).isEqualTo(dto.getIpAddress());
	    assertThat(entity.getUserAgent()).isEqualTo(dto.getUserAgent());
	}
	 
	@Test
	void updateEntity_shouldMergeUpdateDtoIntoEntity() {
	    Session entity = new Session();
	    entity.setId(10L);
	    entity.setSessionId("oldSession");
	    entity.setCreatedAt(LocalDateTime.of(2025,1,1,12,0));
	    entity.setExpiredAt(LocalDateTime.of(2025,1,1,14,0));
	    entity.setActive(false);
	    entity.setIpAddress("127.0.0.1");
	    entity.setUserAgent("OldAgent");

	    UpdateSessionDTO dto = new UpdateSessionDTO();
	    dto.setExpiredAt(LocalDateTime.of(2025,8,14,16,0));
	    dto.setActive(true);
	    dto.setIpAddress("192.168.1.2");
	    dto.setUserAgent("Mozilla/5.0");

	    sessionMapper.updateEntity(dto, entity);

	    assertThat(entity.getId()).isEqualTo(10L); // ignore edildi
	    assertThat(entity.getSessionId()).isEqualTo("oldSession"); // ignore edildi
	    assertThat(entity.getCreatedAt()).isEqualTo(LocalDateTime.of(2025,1,1,12,0)); // ignore edildi
	    assertThat(entity.getExpiredAt()).isEqualTo(dto.getExpiredAt());
	    assertThat(entity.isActive()).isEqualTo(dto.isActive());
	    assertThat(entity.getIpAddress()).isEqualTo(dto.getIpAddress());
	    assertThat(entity.getUserAgent()).isEqualTo(dto.getUserAgent());
	} 
	
	@Test
	void toResponseDto_shouldMapEntityToResponseDto() {
	    User user = new User();
	    user.setId(1L);
	    user.setUsername("alperen");

	    Session entity = new Session();
	    entity.setId(10L);
	    entity.setUser(user);
	    entity.setSessionId("sess123");
	    entity.setCreatedAt(LocalDateTime.of(2025,8,14,12,0));
	    entity.setExpiredAt(LocalDateTime.of(2025,8,14,14,0));
	    entity.setActive(true);
	    entity.setIpAddress("192.168.1.1");
	    entity.setUserAgent("Mozilla/5.0");

	    SessionResponseDTO dto = sessionMapper.toResponseDto(entity);

	    assertThat(dto.getId()).isEqualTo(10L);
	    assertThat(dto.getUserId()).isEqualTo(1L);
	    assertThat(dto.getUsername()).isEqualTo("alperen");
	    assertThat(dto.getSessionId()).isEqualTo("sess123");
	    assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
	    assertThat(dto.getExpiredAt()).isEqualTo(entity.getExpiredAt());
	    assertThat(dto.isActive()).isEqualTo(entity.isActive());
	    assertThat(dto.getIpAddress()).isEqualTo(entity.getIpAddress());
	    assertThat(dto.getUserAgent()).isEqualTo(entity.getUserAgent());
	}
	
}
