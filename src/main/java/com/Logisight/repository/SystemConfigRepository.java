package com.Logisight.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.SystemConfig;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    // Key'e göre config bulmak yaygın ihtiyaçtır
    Optional<SystemConfig> findByKey(String key);

    // Eğer lazım olursa, key'e göre var mı kontrol etmek için
    boolean existsByKey(String key);
}
