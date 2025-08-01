package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;

public interface PdfReportService {
	PdfReportResponseDTO createPdfReport(CreatePdfReportDTO createDTO);

    Optional<PdfReportResponseDTO> updatePdfReport(Long id, UpdatePdfReportDTO updateDTO);

    Optional<PdfReportResponseDTO> getPdfReportById(Long id);

    List<PdfReportResponseDTO> getPdfReportsByUserId(Long userId);

    List<PdfReportResponseDTO> searchPdfReportsByName(String reportName);

    List<PdfReportResponseDTO> getPdfReportsByDateRange(LocalDateTime start, LocalDateTime end);

    void deletePdfReport(Long id);
}