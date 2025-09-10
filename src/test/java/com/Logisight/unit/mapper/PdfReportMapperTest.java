package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
 
import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;
import com.Logisight.entity.User;
import com.Logisight.mapper.PdfReportMapper;

public class PdfReportMapperTest {

	private PdfReportMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = Mappers.getMapper(PdfReportMapper.class);
	}
	
	 @Test
	    void toEntity_shouldMapCreateDtoToEntity() {
	        CreatePdfReportDTO dto = new CreatePdfReportDTO();
	        dto.setReportName("Annual Report");
	        dto.setGeneratedAt(LocalDateTime.of(2025, 8, 14, 10, 0));
	        dto.setFilePath("/reports/annual.pdf");
	        dto.setFileSizeBytes(1024L);
	        dto.setGeneratedByUserId(99L); // mapper ignore ediyor

	        PdfReport entity = mapper.toEntity(dto);

	        assertThat(entity.getId()).isNull(); // DB auto-gen
	        assertThat(entity.getGeneratedBy()).isNull(); // servis içinde setlenecek
	        assertThat(entity.getReportName()).isEqualTo(dto.getReportName());
	        assertThat(entity.getGeneratedAt()).isEqualTo(dto.getGeneratedAt());
	        assertThat(entity.getFilePath()).isEqualTo(dto.getFilePath());
	        assertThat(entity.getFileSizeBytes()).isEqualTo(dto.getFileSizeBytes());
	    }

	    @Test
	    void updateEntity_shouldMergeNonNullFields() {
	        PdfReport existing = new PdfReport();
	        existing.setId(1L);
	        existing.setReportName("Old Report");
	        existing.setGeneratedAt(LocalDateTime.of(2025, 1, 1, 9, 0));
	        existing.setFilePath("/old/path.pdf");
	        existing.setFileSizeBytes(500L);
	        existing.setGeneratedBy(null); // servis setler

	        UpdatePdfReportDTO dto = new UpdatePdfReportDTO();
	        dto.setReportName("Updated Report");
	        dto.setGeneratedAt(LocalDateTime.of(2025, 8, 14, 11, 0));
	        dto.setFilePath("/reports/updated.pdf");
	        dto.setFileSizeBytes(2048L);
	        dto.setGeneratedByUserId(77L); // mapper ignore ediyor

	        mapper.updateEntity(dto, existing);

	        assertThat(existing.getId()).isEqualTo(1L); // id korunmalı
	        assertThat(existing.getGeneratedBy()).isNull(); // ignore edildi
	        assertThat(existing.getReportName()).isEqualTo(dto.getReportName());
	        assertThat(existing.getGeneratedAt()).isEqualTo(dto.getGeneratedAt());
	        assertThat(existing.getFilePath()).isEqualTo(dto.getFilePath());
	        assertThat(existing.getFileSizeBytes()).isEqualTo(dto.getFileSizeBytes());
	    }

	    @Test
	    void toResponseDto_shouldMapEntityToResponseDto() {
	        User user = new User();
	        user.setId(42L);
	        user.setUsername("alperen");

	        PdfReport entity = new PdfReport();
	        entity.setId(5L);
	        entity.setReportName("Monthly Report");
	        entity.setGeneratedAt(LocalDateTime.of(2025, 8, 14, 12, 0));
	        entity.setFilePath("/reports/monthly.pdf");
	        entity.setFileSizeBytes(4096L);
	        entity.setGeneratedBy(user);

	        PdfReportResponseDTO dto = mapper.toResponseDto(entity);

	        assertThat(dto.getId()).isEqualTo(entity.getId());
	        assertThat(dto.getReportName()).isEqualTo(entity.getReportName());
	        assertThat(dto.getGeneratedAt()).isEqualTo(entity.getGeneratedAt());
	        assertThat(dto.getFilePath()).isEqualTo(entity.getFilePath());
	        assertThat(dto.getFileSizeBytes()).isEqualTo(entity.getFileSizeBytes());
	        assertThat(dto.getGeneratedByUserId()).isEqualTo(user.getId());
	        assertThat(dto.getGeneratedByUsername()).isEqualTo(user.getUsername());
	    }
	
}
