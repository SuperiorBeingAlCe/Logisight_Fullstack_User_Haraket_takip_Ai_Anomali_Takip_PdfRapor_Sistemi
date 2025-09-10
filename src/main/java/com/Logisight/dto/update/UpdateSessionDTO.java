package com.Logisight.dto.update;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateSessionDTO {

    private LocalDateTime expiredAt;

    @NotNull(message = "Aktiflik durumu belirtilmelidir.")
    private boolean active;

    private String ipAddress;

    private String userAgent;
}
