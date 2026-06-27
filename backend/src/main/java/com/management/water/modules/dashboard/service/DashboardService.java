package com.management.water.modules.dashboard.service;

import com.management.water.modules.telemetry.service.DailyLogService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final DailyLogService dailyLogService;

  public Map<String, Object> getStats(
      String apartmentNumber, LocalDate fromDate, LocalDate toDate) {
    return dailyLogService.getStats(apartmentNumber, fromDate, toDate);
  }

  public List<Map<String, Object>> getMonthlySummary(
      String apartmentNumber, LocalDate fromDate, LocalDate toDate) {
    return dailyLogService.getMonthlySummary(apartmentNumber, fromDate, toDate);
  }
}
