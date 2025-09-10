package com.Logisight.scheculder;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.Logisight.dto.create.CreatePdfReportDTO;
import com.Logisight.report.ReportContext;
import com.Logisight.report.ReportType;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.PdfReportService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor
public class ReportScheduler {
	private final PdfReportService pdfReportService; 
	private final UserRepository userRepository; 
	
	@Scheduled(cron = "0 0 0 * * ?") 
 public void generateDailyReports() {
		userRepository.findAll()
		.forEach(user -> { CreatePdfReportDTO dto = new CreatePdfReportDTO(); 
		dto.setReportName("Daily Report"); 
		dto.setGeneratedAt(LocalDateTime.now());
		dto.setFileSizeBytes(0L);
		dto.setGeneratedByUserId(user.getId());
		ReportContext ctx = ReportContext.builder()
		        .start(LocalDateTime.now().minusDays(1)) // 24 saat önce
		        .end(LocalDateTime.now())
		        .userId(user.getId())
		        .build();
		pdfReportService.createPdfReport(dto, ReportType.DAILY, ctx); });
		} }

