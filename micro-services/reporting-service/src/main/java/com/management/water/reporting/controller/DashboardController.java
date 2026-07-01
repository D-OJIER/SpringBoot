package com.management.water.reporting.controller;

import com.management.water.reporting.service.DashboardService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/stats")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Object> getStats(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestParam(name = "apartmentNumber", required = false) String apartmentNumber,
      @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return dashboardService.getStats(authorizationHeader, apartmentNumber, fromDate, toDate);
  }
}
