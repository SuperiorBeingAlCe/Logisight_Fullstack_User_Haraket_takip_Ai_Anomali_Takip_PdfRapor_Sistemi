package com.Logisight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Kullanıcının tüm bildirimlerini getir, istersen okunma durumuna göre filtrele
    List<Notification> findByRecipientId(Long userId);
    
    List<Notification> findByRecipientIdAndReadFalse(Long userId);

    // Okunmamış bildirim sayısını sorgulamak için
    long countByRecipientIdAndReadFalse(Long userId);
}
