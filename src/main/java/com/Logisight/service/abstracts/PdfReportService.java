package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.report.ReportContext;
import com.Logisight.report.ReportType;

public interface PdfReportService {
    PdfReportResponseDTO createPdfReport(CreatePdfReportDTO createDTO, ReportType type, ReportContext ctx);

    Optional<PdfReportResponseDTO> updatePdfReport(Long id, UpdatePdfReportDTO updateDTO);

    Optional<PdfReportResponseDTO> getPdfReportById(Long id);

    // Artık sayfalı dönecek
    Page<PdfReportResponseDTO> getPdfReportsByUserId(Long userId, Pageable pageable);

    Page<PdfReportResponseDTO> searchPdfReportsByName(String reportName, Pageable pageable);

    Page<PdfReportResponseDTO> getPdfReportsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    void deletePdfReport(Long id);
    
    Page<PdfReportResponseDTO> getAllPdfReports(int page, int size);
}