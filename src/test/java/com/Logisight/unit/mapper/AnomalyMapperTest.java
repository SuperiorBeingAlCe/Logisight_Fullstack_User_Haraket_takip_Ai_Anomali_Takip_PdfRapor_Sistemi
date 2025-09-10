package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.update.UpdateAnomalyDTO;
import com.Logisight.entity.Anomaly;
import com.Logisight.entity.User;
import com.Logisight.entity.UserAction;
import com.Logisight.mapper.AnomalyMapper;

public class AnomalyMapperTest {

	private AnomalyMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = Mappers.getMapper(AnomalyMapper.class);
	}
	 @Test
	    void toEntity_shouldMapCreateDtoToEntity_andLinkUser() {
	        CreateAnomalyDTO dto = new CreateAnomalyDTO();
	        dto.setAnomalyType("BRUTE_FORCE");
	        dto.setDescription("Şüpheli giriş denemeleri tespit edildi");
	        dto.setDetectedAt(LocalDateTime.of(2025, 8, 14, 12, 0));
	        dto.setUserId(99L);
	        dto.setResolved(false);
	        dto.setResolvedAt(null);

	        Anomaly entity = mapper.toEntity(dto);

	        assertThat(entity.getId()).isNull();
	        assertThat(entity.getAnomalyType()).isEqualTo(dto.getAnomalyType());
	        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
	        assertThat(entity.getDetectedAt()).isEqualTo(dto.getDetectedAt());
	        assertThat(entity.isResolved()).isEqualTo(dto.isResolved());
	        assertThat(entity.getResolvedAt()).isEqualTo(dto.getResolvedAt());
	        assertThat(entity.getUser()).isNotNull();
	        assertThat(entity.getUser().getId()).isEqualTo(dto.getUserId());
	        assertThat(entity.getRelatedActions()).isNullOrEmpty();
	    }

	    @Test
	    void updateEntity_shouldMergeNonNullFields() {
	        Anomaly existing = new Anomaly();
	        existing.setId(1L);
	        existing.setAnomalyType("BRUTE_FORCE"); // ignore
	        existing.setDescription("Eski açıklama"); // ignore
	        existing.setDetectedAt(LocalDateTime.of(2025, 1, 1, 10, 0)); // ignore
	        existing.setResolved(false);
	        existing.setResolvedAt(null);
	        existing.setUser(null); // ignore

	        UpdateAnomalyDTO dto = new UpdateAnomalyDTO();
	        dto.setResolved(true);
	        dto.setResolvedAt(LocalDateTime.of(2025, 8, 14, 15, 0));

	        mapper.updateEntity(dto, existing);

	        assertThat(existing.getId()).isEqualTo(1L);
	        assertThat(existing.getAnomalyType()).isEqualTo("BRUTE_FORCE"); // ignore
	        assertThat(existing.getDescription()).isEqualTo("Eski açıklama"); // ignore
	        assertThat(existing.getDetectedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0)); // ignore
	        assertThat(existing.isResolved()).isEqualTo(dto.getResolved());
	        assertThat(existing.getResolvedAt()).isEqualTo(dto.getResolvedAt());
	    }

	    @Test
	    void toResponseDto_shouldMapEntityToResponseDto() {
	        User user = new User();
	        user.setId(42L);
	        user.setUsername("example");

	        UserAction action1 = new UserAction();
	        action1.setId(101L);
	        action1.setAnomaly(null); // test için null olabilir
	        UserAction action2 = new UserAction();
	        action2.setId(102L);
	        action2.setAnomaly(null);

	        Anomaly entity = new Anomaly();
	        entity.setId(5L);
	        entity.setAnomalyType("RAPID_REQUESTS");
	        entity.setDescription("Hızlı istek tespit edildi");
	        entity.setDetectedAt(LocalDateTime.of(2025, 8, 14, 14, 0));
	        entity.setResolved(true);
	        entity.setResolvedAt(LocalDateTime.of(2025, 8, 14, 15, 0));
	        entity.setUser(user);
	        entity.setRelatedActions(Arrays.asList(action1, action2));

	        AnomalyResponseDTO dto = mapper.toResponseDto(entity);

	        assertThat(dto.getId()).isEqualTo(entity.getId());
	        assertThat(dto.getAnomalyType()).isEqualTo(entity.getAnomalyType());
	        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
	        assertThat(dto.getDetectedAt()).isEqualTo(entity.getDetectedAt());
	        assertThat(dto.isResolved()).isEqualTo(entity.isResolved());
	        assertThat(dto.getResolvedAt()).isEqualTo(entity.getResolvedAt());
	        assertThat(dto.getUserId()).isEqualTo(user.getId());
	        assertThat(dto.getUsername()).isEqualTo(user.getUsername());
	        assertThat(dto.getRelatedUserActionIds()).containsExactly(101L, 102L);
	    }
}
