package com.Logisight.dto.update;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSystemConfigDTO {

    @NotBlank(message = "Değer (value) boş olamaz.")
    private String value;

    private String description;
}
