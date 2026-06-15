package com.management.water.modules.telemetry.controller;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.telemetry.dto.DailyLogCreateRequest;
import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.service.DailyLogService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
public class DailyLogController {

  private final DailyLogService service;

  @PostMapping
  public DailyLog create(@Valid @RequestBody DailyLogCreateRequest request) {
    DailyLog log = new DailyLog();
    log.setLogDate(request.getLogDate());
    log.setTotalLitresConsumed(request.getTotalLitresConsumed());
    log.setGuestCount(request.getGuestCount());
    Apartment apartment = new Apartment();
    apartment.setId(request.getApartmentId());
    log.setApartment(apartment);
    return service.create(log);
  }

  @GetMapping
  public Page<DailyLog> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String apartmentNumber,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate) {
    return service.getPage(page, size, apartmentNumber, fromDate, toDate);
  }
}
