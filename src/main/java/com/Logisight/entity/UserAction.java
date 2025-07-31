package com.Logisight.entity;

import java.time.LocalDateTime;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user-actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAction {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @Column(nullable = false)
	    private String sessionId;

	    @Column(nullable = false)
	    private String actionType; // e.g. PAGE_VIEW, CLICK, FORM_SUBMIT, LOGIN

	    @Column(nullable = false, length = 2048)
	    private String actionDetail; // JSON veya string detay (örn: hangi sayfa, hangi buton)

	    @Column(nullable = false)
	    private LocalDateTime actionTimestamp;

	    @Column(nullable = false)
	    private Long durationMs; // Kullanıcı aksiyonda ne kadar kaldı (milisaniye)

	    @Column(nullable = false)
	    private String ipAddress;

	    @Column
	    private String userAgent;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "anomaly_id")
	    private Anomaly anomaly; // Eğer bu eylem anormal olarak işaretlendiyse ilişkilendir
	}

