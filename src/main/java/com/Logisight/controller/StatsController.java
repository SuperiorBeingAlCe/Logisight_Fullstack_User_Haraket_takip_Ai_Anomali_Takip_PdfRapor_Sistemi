package com.Logisight.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.dto.request.DailyCountDTO;
import com.Logisight.dto.request.SummaryStatsDTO;
import com.Logisight.service.concretes.StatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
	private final StatsService statsService;

    @GetMapping("/logins")
    public List<DailyCountDTO> getLogins(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return statsService.getDailyLogins(start, end);
    }
    
    @GetMapping("/logins/period")
    public List<DailyCountDTO> getLoginsByPeriod(
            @RequestParam String period, // "day", "week", "month"
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return statsService.getLoginsByPeriod(period, start, end);
    }

    @GetMapping("/reports")
    public List<DailyCountDTO> getReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return statsService.getDailyReports(start, end);
    }

    @GetMapping("/summary")
    public SummaryStatsDTO getSummary() {
        return statsService.getSummaryStats();
    }

}
