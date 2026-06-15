package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.waterconfig.dto.WaterRateCreateRequest;
import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.service.WaterRateService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/water-rates")
@RequiredArgsConstructor
public class WaterRateController {

  private final WaterRateService service;

  @PostMapping
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
  public List<WaterRate> getAll() {
    return service.getAll();
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }

  @PutMapping("/{id}")
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
