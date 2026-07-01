package com.management.water.telemetry.controller;

import com.management.water.telemetry.dto.DailyLogCreateRequest;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.service.DailyLogService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
public class DailyLogController {

  private final DailyLogService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public DailyLog create(@Valid @RequestBody DailyLogCreateRequest request) {
    DailyLog log = DailyLog.builder()
        .logDate(request.getLogDate())
        .totalLitresConsumed(request.getTotalLitresConsumed())
        .guestCount(request.getGuestCount())
        .apartmentId(request.getApartmentId())
        .build();
    return service.create(log);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Page<DailyLog> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
      @RequestParam(defaultValue = "logDate") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDir) {
    return service.getPage(page, size, apartmentNumber, fromDate, toDate, sortBy, sortDir);
  }
}
