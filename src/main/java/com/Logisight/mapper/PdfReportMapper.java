package com.Logisight.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;

@Mapper(componentModel = "spring")
public interface PdfReportMapper {

    // Create DTO -> Entity
	 @Mapping(target = "id", ignore = true)
	    @Mapping(target = "generatedBy", ignore = true)
    PdfReport toEntity(CreatePdfReportDTO dto);

    // Update DTO -> Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedBy", ignore = true) // servis içinde setlenecek
    PdfReport updateEntity(UpdatePdfReportDTO dto, @MappingTarget PdfReport entity);

    // Entity -> Response DTO
    @Mapping(target = "generatedByUserId", source = "generatedBy.id")
    @Mapping(target = "generatedByUsername", source = "generatedBy.username")
    PdfReportResponseDTO toResponseDto(PdfReport entity);
}