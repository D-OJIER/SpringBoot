package com.management.water.waterconfig.controller;

import com.management.water.waterconfig.dto.WaterRateCreateRequest;
import com.management.water.waterconfig.entity.WaterRate;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.service.WaterRateService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/water-rates")
@RequiredArgsConstructor
public class WaterRateController {

  private final WaterRateService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public WaterRate create(@Valid @RequestBody WaterRateCreateRequest request) {
    WaterRate rate = new WaterRate();
    rate.setMinLitres(request.getMinLitres());
    rate.setMaxLitres(request.getMaxLitres());
    rate.setRatePerLitre(request.getRatePerLitre());
    rate.setEffectiveFrom(request.getEffectiveFrom());
    rate.setEffectiveTo(request.getEffectiveTo());
    WaterSource source = new WaterSource();
    source.setId(request.getSourceId());
    rate.setSource(source);
    return service.create(rate);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<WaterRate> getAll() {
    return service.getAll();
  }

  @GetMapping("/source/{sourceId}/date/{date}")
  @PreAuthorize("isAuthenticated()")
  public List<WaterRate> getRatesBySourceAndDate(
      @PathVariable Long sourceId,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return service.getRatesBySourceAndDate(sourceId, date);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public WaterRate update(
      @PathVariable Long id, @Valid @RequestBody WaterRateCreateRequest request) {
    WaterRate updated = new WaterRate();
    updated.setMinLitres(request.getMinLitres());
    updated.setMaxLitres(request.getMaxLitres());
    updated.setRatePerLitre(request.getRatePerLitre());
    updated.setEffectiveFrom(request.getEffectiveFrom());
    updated.setEffectiveTo(request.getEffectiveTo());
    WaterSource source = new WaterSource();
    source.setId(request.getSourceId());
    updated.setSource(source);
    return service.update(id, updated);
  }
}
