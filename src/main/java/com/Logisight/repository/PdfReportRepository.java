package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.PdfReport;

@Repository
public interface PdfReportRepository extends JpaRepository<PdfReport, Long> {
    
	 // Kullanıcıya göre raporları sayfalı şekilde bulmak için
    Page<PdfReport> findByGeneratedById(Long userId, Pageable pageable);
    
    // Rapor adına göre arama, sayfalı ve büyük harf-küçük harf duyarsız
    Page<PdfReport> findByReportNameContainingIgnoreCase(String reportName, Pageable pageable);
    
    // Tarih aralığında oluşturulan raporları sayfalı şekilde getirme
    Page<PdfReport> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT DATE(r.generatedAt) as day, COUNT(r) " +
            "FROM PdfReport r " +
            "WHERE r.generatedAt BETWEEN :start AND :end " +
            "GROUP BY DATE(r.generatedAt) " +
            "ORDER BY day ASC")
     List<Object[]> countReportsByDay(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
     
 

 }