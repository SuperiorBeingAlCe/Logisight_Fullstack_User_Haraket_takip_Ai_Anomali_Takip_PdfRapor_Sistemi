package com.Logisight.report;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Logisight.entity.PdfReport;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Component
public class LoginOnlyReportGenerator implements ReportGenerator{
	 @Override
	    public ReportType getReportType() {
	        return ReportType.LOGIN_ONLY;
	    }

	    @Override
	    public String generate(PdfReport report, ReportContext context) {
	        String fileName = "login-report-" + UUID.randomUUID() + ".pdf";
	        String filePath = "./reports/" + fileName;

	        try (PdfWriter writer = new PdfWriter(filePath);
	             PdfDocument pdfDoc = new PdfDocument(writer);
	             Document document = new Document(pdfDoc)) {

	            document.add(new Paragraph("Login-Only Report"));
	            document.add(new Paragraph("Generated At: " + LocalDateTime.now()));

	            // Burada sadece login aksiyonlarını çekip tabloya basabilirsin
	            document.add(new Paragraph("Login Events: ..."));

	        } catch (Exception e) {
	            throw new BusinessException(ErrorCode.PDF_REPORT_CREATION_FAILED, e.getMessage());
	        }
	        return filePath;
	    }
	}