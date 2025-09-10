package com.Logisight.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleDTO {

    @NotBlank(message = "Rol adı boş olamaz.")
    private String name;
    
    
    
}
