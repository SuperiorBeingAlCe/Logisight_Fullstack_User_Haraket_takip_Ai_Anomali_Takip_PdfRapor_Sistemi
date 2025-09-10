package com.Logisight.dto.update;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdatePdfReportDTO {

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
}
