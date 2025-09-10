package com.Logisight.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryStatsDTO {
    private Long totalUsers;
    private Long totalReports;
    private Long activeUsers;
}