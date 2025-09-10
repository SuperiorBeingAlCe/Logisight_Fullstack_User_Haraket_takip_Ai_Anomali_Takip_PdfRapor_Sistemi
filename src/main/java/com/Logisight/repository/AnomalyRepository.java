package com.Logisight.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.Anomaly;

@Repository
public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {

    // Kullanıcıya göre anomali listesi (sayfalama destekli)
    Page<Anomaly> findByUserId(Long userId, Pageable pageable);

    // Çözülmemiş anomalileri getir (sayfalama destekli)
    Page<Anomaly> findByResolvedFalse(Pageable pageable);

    // Türüne göre anomalileri getir (sayfalama destekli)
    Page<Anomaly> findByAnomalyType(String anomalyType, Pageable pageable);

    // Kullanıcı ve çözülmemiş anomaliler (sayfalama destekli)
    Page<Anomaly> findByUserIdAndResolvedFalse(Long userId, Pageable pageable);

}