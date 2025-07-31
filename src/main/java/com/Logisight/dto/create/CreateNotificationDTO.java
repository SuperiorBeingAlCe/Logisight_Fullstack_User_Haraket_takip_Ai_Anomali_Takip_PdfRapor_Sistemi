package com.Logisight.dto.create;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNotificationDTO {

    @NotBlank(message = "Bildirim mesajı boş olamaz.")
    private String message;

    @NotNull(message = "Oluşturulma zamanı belirtilmelidir.")
    private LocalDateTime createdAt;

    @NotNull(message = "Okunma durumu belirtilmelidir.")
    private Boolean read;

    @NotNull(message = "Alıcı kullanıcı ID'si boş olamaz.")
    private Long recipientId;

    private String link;
}
