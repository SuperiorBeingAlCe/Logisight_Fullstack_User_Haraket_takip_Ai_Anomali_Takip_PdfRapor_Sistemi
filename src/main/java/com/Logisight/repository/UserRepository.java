package com.Logisight.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	 // Username'e göre kullanıcı bul
    Optional<User> findByUsername(String username);

    // Email'e göre kullanıcı bul
    Optional<User> findByEmail(String email);

    // Username'in sistemde zaten var olup olmadığını kontrol et
    boolean existsByUsername(String username);

    // Email'in sistemde zaten var olup olmadığını kontrol et
    boolean existsByEmail(String email);

    // Aktif kullanıcıları listele
    Page<User> findAllByEnabledTrue(Pageable pageable);
    
    long countByEnabledTrue();
}
