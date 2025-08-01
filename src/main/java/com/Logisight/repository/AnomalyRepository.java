package com.Logisight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.Anomaly;

@Repository
public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {

    // Kullanıcıya göre anomali listesi
    List<Anomaly> findByUserId(Long userId);

    // Çözülmemiş anomalileri getir
    List<Anomaly> findByResolvedFalse();

    // Türüne göre anomalileri getir
    List<Anomaly> findByAnomalyType(String anomalyType);

    // Kullanıcı ve çözülmemiş anomaliler
    List<Anomaly> findByUserIdAndResolvedFalse(Long userId);

}