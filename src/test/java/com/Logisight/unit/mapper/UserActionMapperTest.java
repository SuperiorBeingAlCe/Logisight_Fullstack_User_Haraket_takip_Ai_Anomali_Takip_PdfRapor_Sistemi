package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.entity.UserAction;
import com.Logisight.mapper.UserActionMapper;

public class UserActionMapperTest {
	
private UserActionMapper userActionMapper;

@BeforeEach
  void setup() {
	userActionMapper = Mappers.getMapper(UserActionMapper.class);
}

@Test
void toEntity_shouldMapCreateDtoToEntity() {
    CreateUserActionDTO dto = new CreateUserActionDTO();
    dto.setUserId(1L);
    dto.setSessionId("sess123");
    dto.setActionType("LOGIN");
    dto.setActionDetail("User logged in");
    dto.setActionTimestamp(LocalDateTime.of(2025,8,14,12,0));
    dto.setDurationMs(200L);
    dto.setIpAddress("192.168.1.1");
    dto.setUserAgent("Mozilla/5.0");
    dto.setAnomalyId(null);

    UserAction entity = userActionMapper.toEntity(dto);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getUser()).isNull();
    assertThat(entity.getAnomaly()).isNull();
    assertThat(entity.getSessionId()).isEqualTo(dto.getSessionId());
    assertThat(entity.getActionType()).isEqualTo(dto.getActionType());
    assertThat(entity.getActionDetail()).isEqualTo(dto.getActionDetail());
    assertThat(entity.getActionTimestamp()).isEqualTo(dto.getActionTimestamp());
    assertThat(entity.getDurationMs()).isEqualTo(dto.getDurationMs());
    assertThat(entity.getIpAddress()).isEqualTo(dto.getIpAddress());
    assertThat(entity.getUserAgent()).isEqualTo(dto.getUserAgent());
}

@Test
void updateEntity_shouldMergeUpdateDtoIntoEntity() {
    UserAction entity = UserAction.builder()
            .id(10L)
            .sessionId("oldSession")
            .actionType("OLD")
            .actionDetail("Old detail")
            .actionTimestamp(LocalDateTime.of(2025,1,1,12,0))
            .durationMs(100L)
            .ipAddress("127.0.0.1")
            .userAgent("OldAgent")
            .build();

    UpdateUserActionDTO dto = new UpdateUserActionDTO();
    dto.setSessionId("sess456");
    dto.setActionType("LOGIN");
    dto.setActionDetail("User updated login");
    dto.setActionTimestamp(LocalDateTime.of(2025,8,14,12,0));
    dto.setDurationMs(300L);
    dto.setIpAddress("192.168.1.2");
    dto.setUserAgent("Mozilla/5.0");
    dto.setAnomalyId(null);

    userActionMapper.updateEntity(dto, entity);

    assertThat(entity.getId()).isEqualTo(10L); // ignore edildi
    assertThat(entity.getSessionId()).isEqualTo(dto.getSessionId());
    assertThat(entity.getActionType()).isEqualTo(dto.getActionType());
    assertThat(entity.getActionDetail()).isEqualTo(dto.getActionDetail());
    assertThat(entity.getActionTimestamp()).isEqualTo(dto.getActionTimestamp());
    assertThat(entity.getDurationMs()).isEqualTo(dto.getDurationMs());
    assertThat(entity.getIpAddress()).isEqualTo(dto.getIpAddress());
    assertThat(entity.getUserAgent()).isEqualTo(dto.getUserAgent());
}

@Test
void toResponseDto_shouldMapEntityToResponseDto() {
    User user = new User();
    user.setId(1L);
    user.setUsername("example");

    Anomaly anomaly = new Anomaly();
    anomaly.setId(99L);
    anomaly.setAnomalyType("BOT_BEHAVIOR");

    UserAction entity = UserAction.builder()
            .id(10L)
            .user(user)
            .sessionId("sess123")
            .actionType("LOGIN")
            .actionDetail("Action detail")
            .actionTimestamp(LocalDateTime.of(2025,8,14,12,0))
            .durationMs(200L)
            .ipAddress("192.168.1.1")
            .userAgent("Mozilla/5.0")
            .anomaly(anomaly)
            .build();

    UserActionResponseDto dto = userActionMapper.toResponseDto(entity);

    assertThat(dto.getId()).isEqualTo(10L);
    assertThat(dto.getUserId()).isEqualTo(1L);
    assertThat(dto.getUsername()).isEqualTo("example");
    assertThat(dto.getSessionId()).isEqualTo("sess123");
    assertThat(dto.getActionType()).isEqualTo("LOGIN");
    assertThat(dto.getActionDetail()).isEqualTo("Action detail");
    assertThat(dto.getActionTimestamp()).isEqualTo(entity.getActionTimestamp());
    assertThat(dto.getDurationMs()).isEqualTo(200L);
    assertThat(dto.getIpAddress()).isEqualTo("192.168.1.1");
    assertThat(dto.getUserAgent()).isEqualTo("Mozilla/5.0");
    assertThat(dto.getAnomalyId()).isEqualTo(99L);
    assertThat(dto.getAnomalyType()).isEqualTo("BOT_BEHAVIOR");
}

}

