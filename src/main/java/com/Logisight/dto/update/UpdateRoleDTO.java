package com.Logisight.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRoleDTO {
	 @NotBlank(message = "Rol adı boş olamaz.")
	    private String name;
	 
	
}
