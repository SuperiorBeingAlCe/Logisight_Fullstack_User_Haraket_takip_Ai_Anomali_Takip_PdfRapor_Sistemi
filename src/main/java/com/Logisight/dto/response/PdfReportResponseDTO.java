package com.Logisight.dto.response;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PdfReportResponseDTO {

    private Long id;

    private String reportName;

    private LocalDateTime generatedAt;

    private String filePath;

    private Long fileSizeBytes;

    private Long generatedByUserId;

    private String generatedByUsername;
}
