package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Logisight.entity.UserAction;

@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findByUserIdAndActionTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(ua) FROM UserAction ua WHERE ua.user.id = :userId AND ua.actionTimestamp > :since")
    Long countByUserIdAndActionTimestampAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT ua.actionType, COUNT(ua) FROM UserAction ua GROUP BY ua.actionType")
    List<Object[]> countGroupedByActionType();

    List<UserAction> findByIpAddressAndActionTimestampBetween(String ipAddress, LocalDateTime start, LocalDateTime end);

    List<UserAction> findBySessionId(String sessionId);

    List<UserAction> findByAnomalyId(Long anomalyId);


}