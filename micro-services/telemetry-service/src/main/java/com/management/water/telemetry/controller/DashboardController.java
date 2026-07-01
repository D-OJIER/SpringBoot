package com.management.water.telemetry.controller;

import com.management.water.telemetry.service.DailyLogService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DailyLogService dailyLogService;

  @GetMapping("/stats")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Object> getStats(
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return dailyLogService.getStats(apartmentNumber, fromDate, toDate);
  }
}
