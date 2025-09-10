package com.Logisight.report;

import com.Logisight.entity.PdfReport;

public interface ReportGenerator {
	
	 ReportType getReportType();
	    String generate(PdfReport report, ReportContext context);
}
