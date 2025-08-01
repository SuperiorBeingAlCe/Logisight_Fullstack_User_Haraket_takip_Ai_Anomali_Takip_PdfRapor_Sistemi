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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "generatedBy", ignore = true) // servis içinde setlenecek
    PdfReport toEntity(CreatePdfReportDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedAt", ignore = true)
    @Mapping(target = "generatedBy", ignore = true)
    PdfReport updateEntity(UpdatePdfReportDTO dto, @MappingTarget PdfReport entity);

    @Mapping(target = "generatedByUserId", source = "generatedBy.id")
    PdfReportResponseDTO toResponseDto(PdfReport entity);
}