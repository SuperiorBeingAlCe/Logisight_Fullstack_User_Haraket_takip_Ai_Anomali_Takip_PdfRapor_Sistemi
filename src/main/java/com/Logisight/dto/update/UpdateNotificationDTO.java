package com.Logisight.dto.update;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateNotificationDTO {

    @NotNull(message = "Okunma durumu belirtilmelidir.")
    private Boolean read;

    private String link;
}
