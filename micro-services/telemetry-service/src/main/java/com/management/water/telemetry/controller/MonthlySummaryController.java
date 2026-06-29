package com.management.water.telemetry.controller;

import com.management.water.telemetry.service.DailyLogService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monthly-summary")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MonthlySummaryController {

  private final DailyLogService dailyLogService;

  @GetMapping
  public List<Map<String, Object>> getSummary(
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate) {
    return dailyLogService.getMonthlySummary(apartmentNumber, fromDate, toDate);
  }
}
