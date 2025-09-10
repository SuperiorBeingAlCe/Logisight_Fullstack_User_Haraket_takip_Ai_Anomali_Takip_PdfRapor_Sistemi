package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
	// sessionId'ye göre tekil session bulma
    Optional<Session> findBySessionId(String sessionId);
    
    // Aktif sessionları kullanıcıya göre sayfalı listeleme
    Page<Session> findByUserIdAndActiveTrue(Long userId, Pageable pageable);
    
    boolean existsBySessionIdAndActiveTrue(String sessionId);
    
    // Kullanıcıya ait tüm sessionları sayfalı getirme
    Page<Session> findByUserId(Long userId, Pageable pageable);
    
    // Süresi dolmuş ve halen aktif olan sessionları sayfalı listeleme (örneğin temizleme için)
    Page<Session> findByExpiredAtBeforeAndActiveTrue(LocalDateTime now, Pageable pageable);
}