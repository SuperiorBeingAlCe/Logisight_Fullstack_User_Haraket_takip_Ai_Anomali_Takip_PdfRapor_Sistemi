package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.PdfReport;

@Repository
public interface PdfReportRepository extends JpaRepository<PdfReport, Long> {
    
    // Kullanıcıya göre raporları bulmak için
    List<PdfReport> findByGeneratedById(Long userId);
    
    // Rapor adına göre arama (örneğin dosya isimleriyle arama)
    List<PdfReport> findByReportNameContainingIgnoreCase(String reportName);
    
    // Tarih aralığında oluşturulan raporlar
    List<PdfReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
}
