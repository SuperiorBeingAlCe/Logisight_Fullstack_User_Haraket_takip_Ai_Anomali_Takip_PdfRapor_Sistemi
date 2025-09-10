package com.Logisight.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Logisight.dto.request.DailyCountDTO;
import com.Logisight.entity.UserAction;

@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {

	  // Sayfalı kullanıcı ve tarih aralığı
    Page<UserAction> findByUserIdAndActionTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT COUNT(ua) FROM UserAction ua WHERE ua.user.id = :userId AND ua.actionTimestamp > :since")
    Long countByUserIdAndActionTimestampAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT ua.actionType, COUNT(ua) FROM UserAction ua GROUP BY ua.actionType")
    List<Object[]> countGroupedByActionType();

    // Sayfalı IP ve tarih aralığı
    Page<UserAction> findByIpAddressAndActionTimestampBetween(String ipAddress, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Sayfalı sessionId ile arama
    Page<UserAction> findBySessionId(String sessionId, Pageable pageable);

    // Sayfalı anomalyId ile arama
    Page<UserAction> findByAnomalyId(Long anomalyId, Pageable pageable);

    List<UserAction> findAllByUserIdAndActionTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end);
 // Günlük
    @Query("""
        SELECT new com.Logisight.dto.request.DailyCountDTO(
            to_char(date_trunc('day', ua.actionTimestamp), 'YYYY-MM-DD'),
            COUNT(ua.user),
            COUNT(DISTINCT ua.user)
        )
        FROM UserAction ua
        WHERE ua.actionType = 'LOGIN'
          AND ua.actionTimestamp BETWEEN :start AND :end
        GROUP BY date_trunc('day', ua.actionTimestamp)
        ORDER BY date_trunc('day', ua.actionTimestamp) ASC
    """)
    List<DailyCountDTO> countUniqueLoginsByDay(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    // Haftalık
    @Query("""
        SELECT new com.Logisight.dto.request.DailyCountDTO(
            to_char(date_trunc('week', ua.actionTimestamp), 'IYYY-IW'),
            COUNT(ua.user),
            COUNT(DISTINCT ua.user)
        )
        FROM UserAction ua
        WHERE ua.actionType = 'LOGIN'
          AND ua.actionTimestamp BETWEEN :start AND :end
        GROUP BY date_trunc('week', ua.actionTimestamp)
        ORDER BY date_trunc('week', ua.actionTimestamp) ASC
    """)
    List<DailyCountDTO> countUniqueLoginsByWeek(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    // Aylık
    @Query("""
        SELECT new com.Logisight.dto.request.DailyCountDTO(
            to_char(date_trunc('month', ua.actionTimestamp), 'YYYY-MM'),
            COUNT(ua.user),
            COUNT(DISTINCT ua.user)
        )
        FROM UserAction ua
        WHERE ua.actionType = 'LOGIN'
          AND ua.actionTimestamp BETWEEN :start AND :end
        GROUP BY date_trunc('month', ua.actionTimestamp)
        ORDER BY date_trunc('month', ua.actionTimestamp) ASC
    """)
    List<DailyCountDTO> countUniqueLoginsByMonth(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
    }