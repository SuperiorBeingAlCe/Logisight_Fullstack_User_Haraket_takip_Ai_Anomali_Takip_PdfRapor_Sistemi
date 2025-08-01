package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    // sessionId'ye göre tekil session bulma
    Optional<Session> findBySessionId(String sessionId);
    
    // Aktif sessionları kullanıcıya göre listeleme
    List<Session> findByUserIdAndActiveTrue(Long userId);
    
    // Kullanıcıya ait tüm sessionları getirme
    List<Session> findByUserId(Long userId);
    
    // Süresi dolmuş ve halen aktif olan sessionları listeleme (örneğin temizleme için)
    List<Session> findByExpiredAtBeforeAndActiveTrue(LocalDateTime now);
}