package com.Logisight.report;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportContext {
  
	 private LocalDateTime start;
	 private LocalDateTime end;
	 private Long userId;
	 private String filterType;
}
