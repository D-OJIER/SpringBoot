package com.management.water.reporting.controller;

import com.management.water.reporting.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monthly-summary")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MonthlySummaryController {

  private final DashboardService dashboardService;

  @GetMapping
  public List<Map<String, Object>> getSummary(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(name = "apartmentNumber", required = false) String apartmentNumber,
      @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) LocalDate toDate) {
    return dashboardService.getMonthlySummary(authorizationHeader, apartmentNumber, fromDate, toDate);
  }
}
