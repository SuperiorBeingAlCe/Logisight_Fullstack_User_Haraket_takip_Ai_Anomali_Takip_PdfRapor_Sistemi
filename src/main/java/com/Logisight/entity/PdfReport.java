package com.Logisight.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdf-reports")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PdfReport {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String reportName;

	    @Column(nullable = false)
	    private LocalDateTime generatedAt;

	    @Column(nullable = false)
	    private String filePath;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "generated_by_user_id", nullable = false)
	    @JsonIgnoreProperties({"actions", "roles"})
	    private User generatedBy;

	    @Column(nullable = false)
	    private Long fileSizeBytes;
}
