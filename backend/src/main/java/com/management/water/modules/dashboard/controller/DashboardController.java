package com.management.water.modules.dashboard.controller;

import com.management.water.modules.dashboard.service.DashboardService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/stats")
  public Map<String, Object> getStats(
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate) {
    return dashboardService.getStats(apartmentNumber, fromDate, toDate);
  }
}
