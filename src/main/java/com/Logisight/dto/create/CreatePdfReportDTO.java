package com.Logisight.dto.create;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.Logisight.report.ReportType;

@Data
public class CreatePdfReportDTO {

    @NotBlank(message = "Rapor adı boş olamaz.")
    private String reportName;

    @NotNull(message = "Oluşturulma zamanı belirtilmelidir.")
    private LocalDateTime generatedAt;

    @NotBlank(message = "Dosya yolu boş olamaz.")
    private String filePath;

    @NotNull(message = "Dosya boyutu (byte) belirtilmelidir.")
    @Positive(message = "Dosya boyutu pozitif olmalıdır.")
    private Long fileSizeBytes;

    private Long generatedByUserId;
    
    @NotNull
    private ReportType type;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}
