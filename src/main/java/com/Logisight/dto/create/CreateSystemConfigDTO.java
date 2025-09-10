package com.Logisight.dto.create;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSystemConfigDTO {

    @NotBlank(message = "Anahtar (key) boş olamaz.")
    private String key;

    @NotBlank(message = "Değer (value) boş olamaz.")
    private String value;

    private String description;
    }
