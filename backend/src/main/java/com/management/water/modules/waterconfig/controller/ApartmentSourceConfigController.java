package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.waterconfig.dto.ApartmentSourceConfigCreateRequest;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.service.ApartmentSourceConfigService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apartment-source-configs")
@RequiredArgsConstructor
public class ApartmentSourceConfigController {

  private final ApartmentSourceConfigService service;

  @PostMapping
  public ApartmentSourceConfig create(
      @Valid @RequestBody ApartmentSourceConfigCreateRequest request) {
    ApartmentSourceConfig config = new ApartmentSourceConfig();
    config.setRatioPercent(request.getRatioPercent());
    Apartment apartment = new Apartment();
    apartment.setId(request.getApartmentId());
    config.setApartment(apartment);
    WaterSource source = new WaterSource();
    source.setId(request.getSourceId());
    config.setSource(source);
    return service.create(config);
  }

  @GetMapping
  public List<ApartmentSourceConfig> getAll() {
    return service.getAll();
  }
}
