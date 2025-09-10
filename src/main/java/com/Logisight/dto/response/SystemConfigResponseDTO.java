package com.Logisight.dto.response;
import lombok.Data;

@Data
public class SystemConfigResponseDTO {

    private Long id;

    private String key;

    private String value;

    private String description;
}
