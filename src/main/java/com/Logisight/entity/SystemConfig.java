package com.Logisight.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system-configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false)
	    private String key;  // e.g. "ANOMALY_THRESHOLD_REQUESTS_PER_MINUTE"

	    @Column(nullable = false)
	    private String value;

	    @Column
	    private String description;
}
