package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Logisight.dto.request.DailyCountDTO;
import com.Logisight.dto.request.SummaryStatsDTO;
import com.Logisight.repository.PdfReportRepository;
import com.Logisight.repository.UserActionRepository;
import com.Logisight.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {
	
	private final UserActionRepository userActionRepository;
    private final PdfReportRepository pdfReportRepository;
    private final UserRepository userRepository;
    public List<DailyCountDTO> getDailyLogins(LocalDateTime start, LocalDateTime end) {
        // Artık JPQL DTO döndürdüğü için direkt return
        return userActionRepository.countUniqueLoginsByDay(start, end);
    }

    public List<DailyCountDTO> getLoginsByPeriod(String period, LocalDateTime start, LocalDateTime end) {
        return switch (period) {
            case "day" -> userActionRepository.countUniqueLoginsByDay(start, end);
            case "week" -> userActionRepository.countUniqueLoginsByWeek(start, end);
            case "month" -> userActionRepository.countUniqueLoginsByMonth(start, end);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    public List<DailyCountDTO> getDailyReports(LocalDateTime start, LocalDateTime end) {
        // Eğer pdfReportRepository native query kullanıyorsa, null yerine 0L veya farklı bir mantık verebilirsin
        return pdfReportRepository.countReportsByDay(start, end).stream()
                .map(obj -> new DailyCountDTO(obj[0].toString(), (Long) obj[1], 0L))
                .toList();
    }

    public SummaryStatsDTO getSummaryStats() { 
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countByEnabledTrue();
        Long totalReports = pdfReportRepository.count();
        return new SummaryStatsDTO(totalUsers, totalReports, activeUsers);
    }
}