package com.Logisight.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.aspect.CapturePhase;
import com.Logisight.aspect.LogUserAction;
import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;
import com.Logisight.report.ReportContext;
import com.Logisight.service.abstracts.PdfReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf-reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class PdfReportController {
	private final PdfReportService pdfReportService;

    @PostMapping
    public ResponseEntity<PdfReportResponseDTO> createPdfReport(@RequestBody @Valid CreatePdfReportDTO createDTO) {

        ReportContext ctx = ReportContext.builder()
                .start(createDTO.getStart())
                .end(createDTO.getEnd())
                .userId(createDTO.getGeneratedByUserId())
                .build();

        PdfReportResponseDTO response = pdfReportService.createPdfReport(createDTO, createDTO.getType(), ctx);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @LogUserAction(
        actionType = "PDF_REPORT_UPDATE",
        dynamicDetail = true,
        entityClass = PdfReport.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<PdfReportResponseDTO> updatePdfReport(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePdfReportDTO updateDTO) {
        return pdfReportService.updatePdfReport(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<PdfReportResponseDTO>> getAllPdfReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PdfReportResponseDTO> reportsPage = pdfReportService.getAllPdfReports(page, size);
        return ResponseEntity.ok(reportsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdfReportResponseDTO> getPdfReportById(@PathVariable Long id) {
        return pdfReportService.getPdfReportById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Page<PdfReportResponseDTO>> getPdfReportsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<PdfReportResponseDTO> page = pdfReportService.getPdfReportsByUserId(userId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PdfReportResponseDTO>> searchPdfReportsByName(
            @RequestParam String reportName,
            Pageable pageable) {
        Page<PdfReportResponseDTO> page = pdfReportService.searchPdfReportsByName(reportName, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<Page<PdfReportResponseDTO>> getPdfReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        Page<PdfReportResponseDTO> page = pdfReportService.getPdfReportsByDateRange(start, end, pageable);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @LogUserAction(
        actionType = "PDF_REPORT_DELETE",
        entityClass = PdfReport.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<Void> deletePdfReport(@PathVariable Long id) {
        pdfReportService.deletePdfReport(id);
        return ResponseEntity.noContent().build();
    }
}