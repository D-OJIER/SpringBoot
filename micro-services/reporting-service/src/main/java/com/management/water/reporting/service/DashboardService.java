package com.management.water.reporting.service;

import com.management.water.reporting.client.TelemetryServiceClient;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final TelemetryServiceClient telemetryClient;

  public Map<String, Object> getStats(String authHeader, String apartmentNumber, LocalDate fromDate, LocalDate toDate) {
    return telemetryClient.getStats(authHeader, apartmentNumber, fromDate, toDate);
  }

  public List<Map<String, Object>> getMonthlySummary(String authHeader, String apartmentNumber, LocalDate fromDate, LocalDate toDate) {
    return telemetryClient.getSummary(authHeader, apartmentNumber, fromDate, toDate);
  }
}
