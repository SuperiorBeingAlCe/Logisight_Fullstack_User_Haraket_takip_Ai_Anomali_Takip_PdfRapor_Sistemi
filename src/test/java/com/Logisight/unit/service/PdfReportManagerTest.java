package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.PdfReportMapper;
import com.Logisight.report.ReportContext;
import com.Logisight.report.ReportGenerator;
import com.Logisight.report.ReportGeneratorFactory;
import com.Logisight.report.ReportType;
import com.Logisight.repository.PdfReportRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.concretes.PdfReportManager;

@ExtendWith(MockitoExtension.class)	
public class PdfReportManagerTest {
	@InjectMocks
    private PdfReportManager pdfReportManager;

    @Mock
    private PdfReportRepository reportRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PdfReportMapper mapper;
    
    @Mock
    private ReportGeneratorFactory generatorFactory;
    
    @Mock
    private ReportGenerator reportGenerator;

    private User user;
    private PdfReport report;
    private PdfReportResponseDTO responseDTO;
    

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        report = new PdfReport();
        report.setId(100L);
        report.setGeneratedBy(user);
        report.setReportName("Test Report");
        report.setGeneratedAt(LocalDateTime.now());

        responseDTO = new PdfReportResponseDTO();
        responseDTO.setId(report.getId());
        responseDTO.setGeneratedByUserId(user.getId());
        responseDTO.setReportName(report.getReportName());
    }
    @Test
    void createPdfReport_success() {
        // GIVEN
        CreatePdfReportDTO createDTO = new CreatePdfReportDTO();
        createDTO.setGeneratedByUserId(user.getId());

        ReportType reportType = ReportType.DAILY;
        ReportContext context = ReportContext.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .userId(user.getId())
                .build();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toEntity(createDTO)).thenReturn(report);
        when(reportRepo.save(report)).thenReturn(report);
        when(mapper.toResponseDto(report)).thenReturn(responseDTO);

        when(generatorFactory.getGenerator(reportType)).thenReturn(reportGenerator);
        when(reportGenerator.generate(any(PdfReport.class), any(ReportContext.class)))
                .thenReturn("./reports/fake-report.pdf");

        // WHEN
        PdfReportResponseDTO result = pdfReportManager.createPdfReport(createDTO, reportType, context);

        // THEN
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getReportName(), result.getReportName());

        verify(userRepo).findById(user.getId());
        verify(mapper).toEntity(createDTO);
        verify(reportRepo).save(report);
        verify(mapper).toResponseDto(report);
        verify(generatorFactory).getGenerator(reportType);
        verify(reportGenerator).generate(any(PdfReport.class), any(ReportContext.class));
    }


    @Test
    void updatePdfReport_success() {
        UpdatePdfReportDTO updateDTO = new UpdatePdfReportDTO();
        updateDTO.setReportName("Updated Name");

        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        doAnswer(invocation -> {
            report.setReportName(updateDTO.getReportName());
            return null;
        }).when(mapper).updateEntity(updateDTO, report);

        when(reportRepo.save(report)).thenReturn(report);
        when(mapper.toResponseDto(report)).thenReturn(responseDTO);

        Optional<PdfReportResponseDTO> result = pdfReportManager.updatePdfReport(report.getId(), updateDTO);

        assertTrue(result.isPresent());
        assertEquals(report.getReportName(), updateDTO.getReportName());
        verify(reportRepo).save(report);
    }

    @Test
    void getPdfReportById_found() {
        when(reportRepo.findById(report.getId())).thenReturn(Optional.of(report));
        when(mapper.toResponseDto(report)).thenReturn(responseDTO);

        Optional<PdfReportResponseDTO> result = pdfReportManager.getPdfReportById(report.getId());

        assertTrue(result.isPresent());
        assertEquals(report.getId(), result.get().getId());
    }

    @Test
    void getPdfReportsByUserId_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PdfReport> page = new PageImpl<>(List.of(report));
        when(reportRepo.findByGeneratedById(user.getId(), pageable)).thenReturn(page);
        when(mapper.toResponseDto(report)).thenReturn(responseDTO);

        Page<PdfReportResponseDTO> result = pdfReportManager.getPdfReportsByUserId(user.getId(), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responseDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getPdfReportsByDateRange_invalidRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> pdfReportManager.getPdfReportsByDateRange(start, end, pageable));

        assertEquals(ErrorCode.PDF_REPORT_INVALID_DATE_RANGE, ex.getErrorCode());
    }

    @Test
    void deletePdfReport_success() {
        when(reportRepo.existsById(report.getId())).thenReturn(true);

        pdfReportManager.deletePdfReport(report.getId());

        verify(reportRepo).deleteById(report.getId());
    }
}
