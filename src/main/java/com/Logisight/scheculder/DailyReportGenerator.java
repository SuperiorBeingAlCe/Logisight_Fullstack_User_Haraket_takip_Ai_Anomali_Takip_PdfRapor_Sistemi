package com.Logisight.scheculder;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Logisight.entity.PdfReport;
import com.Logisight.entity.UserAction;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.report.ReportContext;
import com.Logisight.report.ReportGenerator;
import com.Logisight.report.ReportType;
import com.Logisight.repository.UserActionRepository;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyReportGenerator implements ReportGenerator{

	 private final UserActionRepository userActionRepository;
	
	 @Override
	    public ReportType getReportType() {
	        return ReportType.DAILY;
	    }
	 @Override
	    public String generate(PdfReport report, ReportContext context) {
	        String fileName = "daily-report-" + UUID.randomUUID() + ".pdf";
	        String filePath = "./reports/" + fileName;

	        try (PdfWriter writer = new PdfWriter(filePath);
	             PdfDocument pdfDoc = new PdfDocument(writer);
	             Document document = new Document(pdfDoc)) 
	        
	        {
	        	InputStream fontStream = getClass().getResourceAsStream("/fonts/dejavu-fonts-ttf-2.37/ttf/DejaVuSans.ttf");
	        	if (fontStream == null) {
	        	    throw new RuntimeException("Font bulunamadı! Lütfen resources/fonts/DejaVuSans.ttf yolunu kontrol et.");
	        	}
	        	
	        	PdfFont font = PdfFontFactory.createFont(
	        	        fontStream.readAllBytes(),
	        	        PdfEncodings.IDENTITY_H,
	        	        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
	        	);
	        	
	        	  // Başlık
	            document.add(new Paragraph("Daily Report")
	            		.setFont(font)
	                    .setFontSize(22)
	                    .setBold()
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setMarginBottom(10));

	            // Özet bilgiler
	            document.add(new Paragraph("Generated At: " +
	                    context.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
	            		.setFont(font)
	                    .setFontSize(12)
	                    .setTextAlignment(TextAlignment.RIGHT));

	            document.add(new Paragraph("User ID: " +
	                    (context.getUserId() != null ? context.getUserId() : "ALL"))
	            		.setFont(font)
	                    .setFontSize(12)
	                    .setTextAlignment(TextAlignment.LEFT));

	            document.add(new Paragraph("Period: " +
	                    context.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
	                    " → " + context.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
	            		.setFont(font)
	                    .setFontSize(12)
	                    .setTextAlignment(TextAlignment.LEFT));

	            document.add(new Paragraph(" ")); // Boşluk

	            // User Actions çek
	            List<UserAction> actions = userActionRepository
	                    .findAllByUserIdAndActionTimestampBetween(
	                            context.getUserId(),
	                            context.getStart(),
	                            context.getEnd()
	                    );

	            if (actions.isEmpty()) {
	                document.add(new Paragraph("No actions found in the last 24 hours.").setItalic());
	            } else {
	                // Tablo oluştur
	                Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 5}))
	                        .useAllAvailableWidth();

	                // Header
	                table.addHeaderCell(new Cell().add(new Paragraph("Action Type").setFont(font).setBold())
	                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
	                table.addHeaderCell(new Cell().add(new Paragraph("Timestamp").setFont(font).setBold())
	                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
	                table.addHeaderCell(new Cell().add(new Paragraph("Details").setFont(font).setBold())
	                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));

	                // Satırlar
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	                for (UserAction action : actions) {
	                    table.addCell(new Cell().add(new Paragraph(action.getActionType()).setFont(font)));
	                    table.addCell(new Cell().add(new Paragraph(action.getActionTimestamp().format(formatter)).setFont(font)));
	                    table.addCell(new Cell().add(new Paragraph(action.getActionDetail()).setFont(font)));
	                }

	                document.add(table);
	            }

	        } catch (Exception e) {
	            throw new BusinessException(ErrorCode.PDF_REPORT_CREATION_FAILED, e.getMessage());
	        }

	        return filePath;
	    }
	}