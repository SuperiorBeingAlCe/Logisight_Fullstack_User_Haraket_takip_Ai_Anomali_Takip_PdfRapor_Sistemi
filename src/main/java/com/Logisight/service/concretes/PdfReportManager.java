package com.Logisight.service.concretes;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.dto.response.PdfReportResponseDTO;
import com.Logisight.dto.update.UpdatePdfReportDTO;
import com.Logisight.entity.PdfReport;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.PdfReportMapper;
import com.Logisight.report.ReportContext;
import com.Logisight.report.ReportGeneratorFactory;
import com.Logisight.report.ReportType;
import com.Logisight.repository.PdfReportRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.PdfReportService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class PdfReportManager implements PdfReportService {

    private final PdfReportRepository reportRepo;
    private final UserRepository userRepo;
    private final PdfReportMapper mapper;
    private final ReportGeneratorFactory generatorFactory;
    
    @Value("${pdf.report.directory:./reports}")
    private String pdfDirectory;
	          	
    @Override
    public PdfReportResponseDTO createPdfReport(CreatePdfReportDTO createDTO, ReportType type, ReportContext ctx) {
    	 User user = (createDTO.getGeneratedByUserId() != null)
    	            ? userRepo.findById(createDTO.getGeneratedByUserId())
    	                .orElseThrow(() -> new BusinessException(ErrorCode.PDF_REPORT_ASSOCIATED_USER_NOT_FOUND))
    	            : userRepo.findByUsername("system")
    	                .orElseThrow(() -> new BusinessException(ErrorCode.PDF_REPORT_ASSOCIATED_USER_NOT_FOUND));

    	        PdfReport entity = mapper.toEntity(createDTO);
    	        entity.setGeneratedBy(user);

    	        // içerik generator’a gitti
    	        String filePath = generatorFactory.getGenerator(type).generate(entity, ctx);
    	        entity.setFilePath(filePath);

    	        PdfReport saved = reportRepo.save(entity);
    	        return mapper.toResponseDto(saved);
    	    }
    
    

    @Override
    @CachePut(value = "pdfReports", key = "#id")
    public Optional<PdfReportResponseDTO> updatePdfReport(Long id, UpdatePdfReportDTO updateDTO) {
        PdfReport entity = reportRepo.findById(id)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.PDF_REPORT_NOT_FOUND));

        mapper.updateEntity(updateDTO, entity);

        PdfReport updated;
        try {
            updated = reportRepo.save(entity);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_UPDATE_FAILED, ex.getMessage());
        }

        return Optional.of(mapper.toResponseDto(updated));
    }

    @Override
    @Cacheable(value = "pdfReports", key = "#id")
    public Optional<PdfReportResponseDTO> getPdfReportById(Long id) {
        return reportRepo.findById(id)
            .map(mapper::toResponseDto);
    }

    // Artık pageable parametre alıyor ve Page dönüyor
    @Override
    @Cacheable(value = "pdfReportsByUser", key = "#userId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PdfReportResponseDTO> getPdfReportsByUserId(Long userId, Pageable pageable) {
        return reportRepo.findByGeneratedById(userId, pageable)
            .map(mapper::toResponseDto);
    }
    
    public Map<LocalDate, Long> getDailyReportCounts(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = reportRepo.countReportsByDay(start, end);

        Map<LocalDate, Long> dailyCounts = new LinkedHashMap<>();
        for (Object[] row : results) {
            LocalDate day = ((java.sql.Date) row[0]).toLocalDate();
            Long count = (Long) row[1];
            dailyCounts.put(day, count);
        }
        return dailyCounts;
    }

    
    @Override
    @Cacheable(value = "pdfReportsByName", key = "#reportName + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PdfReportResponseDTO> searchPdfReportsByName(String reportName, Pageable pageable) {
        return reportRepo.findByReportNameContainingIgnoreCase(reportName, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "pdfReportsByDateRange", key = "#start.toString() + '-' + #end.toString() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PdfReportResponseDTO> getPdfReportsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (start.isAfter(end)) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_INVALID_DATE_RANGE);
        }
        return reportRepo.findByGeneratedAtBetween(start, end, pageable)
            .map(mapper::toResponseDto);
    }

    @Override
    @CacheEvict(value = "pdfReports", key = "#id")
    public void deletePdfReport(Long id) {
        if (!reportRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.PDF_REPORT_NOT_FOUND);
        }

        try {
            reportRepo.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(
                ErrorCode.PDF_REPORT_DELETION_FAILED, ex.getMessage());
        }
    }


  
    	@Override
    	@Cacheable(value = "allPdfReportsPage", key = "#page + '-' + #size")
    	public Page<PdfReportResponseDTO> getAllPdfReports(int page, int size) {

    	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "generatedAt"));

    	    File reportsDir = new File("reports");
    	    if (!reportsDir.exists() || !reportsDir.isDirectory()) {
    	        return Page.empty(pageable);
    	    }

    	    File[] pdfFiles = reportsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
    	    if (pdfFiles == null || pdfFiles.length == 0) {
    	        return Page.empty(pageable);
    	    }

    	    List<File> sortedFiles = Arrays.stream(pdfFiles)
    	            .sorted(Comparator.comparingLong(File::lastModified).reversed())
    	            .collect(Collectors.toList());

    	    int start = (int) pageable.getOffset();
    	    int end = Math.min((start + pageable.getPageSize()), sortedFiles.size());
    	    List<File> pageContent = sortedFiles.subList(start, end);

    	    List<PdfReportResponseDTO> dtoList = pageContent.stream().map(file -> {
    	        PdfReportResponseDTO dto = new PdfReportResponseDTO();
    	        dto.setId(null); // backend’de henüz ID yoksa null
    	        dto.setReportName(file.getName());
    	        dto.setFilePath(file.getAbsolutePath());
    	        dto.setFileSizeBytes(file.length());
    	        dto.setGeneratedAt(LocalDateTime.ofInstant(
    	                Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()));
    	        dto.setGeneratedByUserId(null); // user bilgisi yoksa null
    	        dto.setGeneratedByUsername(null); // user bilgisi yoksa null
    	        return dto;
    	    }).collect(Collectors.toList());

    	    return new PageImpl<>(dtoList, pageable, sortedFiles.size());
    	}
}
