package com.Logisight.dto.update;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnomalyUpdateDto {
	 @NotNull(message = "Durum boş olamaz")
	    private Boolean resolved;
}
