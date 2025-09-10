package com.Logisight.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "anomalies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anomaly {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String anomalyType;  // e.g. BOT_BEHAVIOR, BRUTE_FORCE, RAPID_REQUESTS

	    @Column(nullable = false)
	    private String description;  // Detaylı açıklama

	    @Column(nullable = false)
	    private LocalDateTime detectedAt;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @OneToMany(mappedBy = "anomaly", cascade = CascadeType.ALL)
	    private List<UserAction> relatedActions = new ArrayList<>();

	    @Column(nullable = false)
	    private boolean resolved;

	    @Column
	    private LocalDateTime resolvedAt;
	}
