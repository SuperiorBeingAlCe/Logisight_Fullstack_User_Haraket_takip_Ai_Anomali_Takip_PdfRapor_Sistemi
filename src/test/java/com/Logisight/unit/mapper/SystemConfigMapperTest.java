package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateSystemConfigDTO;
import com.Logisight.dto.response.SystemConfigResponseDTO;
import com.Logisight.dto.update.UpdateSystemConfigDTO;
import com.Logisight.entity.SystemConfig;
import com.Logisight.mapper.SystemConfigMapper;

public class SystemConfigMapperTest {

	private SystemConfigMapper systemConfigMapper;
	
	@BeforeEach
	void setup() {
		systemConfigMapper = Mappers.getMapper(SystemConfigMapper.class);
	}
	
	@Test
	void toEntity_shouldMapCreateDtoToEntity() {
	    CreateSystemConfigDTO dto = new CreateSystemConfigDTO();
	    dto.setValue("someValue");

	    SystemConfig entity = systemConfigMapper.toEntity(dto);

	    assertThat(entity.getId()).isNull();
	    assertThat(entity.getKey()).isEqualTo(dto.getKey()); // eğer DTO’da key varsa
	    assertThat(entity.getValue()).isEqualTo(dto.getValue());
	}
	
	@Test
	void updateEntity_shouldMergeUpdateDtoIntoEntity() {
	    SystemConfig entity = new SystemConfig();
	    entity.setId(1L);
	    entity.setKey("SYSTEM_TIMEOUT");
	    entity.setValue("30");

	    UpdateSystemConfigDTO dto = new UpdateSystemConfigDTO();
	    dto.setValue("60"); // sadece value değişiyor

	    systemConfigMapper.updateEntity(dto, entity);

	    assertThat(entity.getId()).isEqualTo(1L); // ignore edildi
	    assertThat(entity.getKey()).isEqualTo("SYSTEM_TIMEOUT"); // ignore edildi
	    assertThat(entity.getValue()).isEqualTo("60");
	}
	
	@Test
	void toResponseDto_shouldMapEntityToResponseDto() {
	    SystemConfig entity = new SystemConfig();
	    entity.setId(1L);
	    entity.setKey("SYSTEM_TIMEOUT");
	    entity.setValue("60");

	    SystemConfigResponseDTO dto = systemConfigMapper.toResponseDto(entity);

	    assertThat(dto.getId()).isEqualTo(1L);
	    assertThat(dto.getKey()).isEqualTo("SYSTEM_TIMEOUT");
	    assertThat(dto.getValue()).isEqualTo("60");
	}
	
}
