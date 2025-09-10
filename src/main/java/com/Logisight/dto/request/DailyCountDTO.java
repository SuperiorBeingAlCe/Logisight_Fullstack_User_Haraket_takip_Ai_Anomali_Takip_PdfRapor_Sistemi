package com.Logisight.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyCountDTO {
	
	 private String period;
	    private Long count;        // Toplam login sayısı
	    private Long uniqueUsers;  // Seçili periyottaki benzersiz kullanıcı sayısı
	}