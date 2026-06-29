package com.management.water.waterconfig.controller;

import com.management.water.waterconfig.dto.ApartmentSourceConfigCreateRequest;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.service.ApartmentSourceConfigService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartment-source-configs")
@RequiredArgsConstructor
public class ApartmentSourceConfigController {

  private final ApartmentSourceConfigService service;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ApartmentSourceConfig create(
      @Valid @RequestBody ApartmentSourceConfigCreateRequest request) {
    ApartmentSourceConfig config = ApartmentSourceConfig.builder()
        .ratioPercent(request.getRatioPercent())
        .apartmentId(request.getApartmentId())
        .source(WaterSource.builder().id(request.getSourceId()).build())
        .build();

    return service.create(config);
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<ApartmentSourceConfig> getAll() {
    return service.getAll();
  }

  @GetMapping("/apartment/{apartmentId}")
  @PreAuthorize("isAuthenticated()")
  public List<ApartmentSourceConfig> getByApartmentId(@PathVariable Long apartmentId) {
    return service.getByApartmentId(apartmentId);
  }
}
