package com.Logisight.report;

import java.util.List;

import org.springframework.stereotype.Component;

import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportGeneratorFactory {
	private final List<ReportGenerator> generators;

    public ReportGenerator getGenerator(ReportType type) {
    	System.out.println("Available generators: " + generators);
        return generators.stream()
                .filter(gen -> gen.getReportType() == type)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PDF_REPORT_TYPE_NOT_FOUND));
    }
}
