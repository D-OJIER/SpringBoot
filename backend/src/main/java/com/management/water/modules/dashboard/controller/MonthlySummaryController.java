package com.management.water.modules.dashboard.controller;

import com.management.water.modules.dashboard.service.DashboardService;
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

  private final DashboardService dashboardService;

  @GetMapping
  public List<Map<String, Object>> getSummary(
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate) {
    return dashboardService.getMonthlySummary(apartmentNumber, fromDate, toDate);
  }
}
