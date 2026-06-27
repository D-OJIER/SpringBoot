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
      
    ApartmentSourceConfig config = ApartmentSourceConfig.builder()
        .ratioPercent(request.getRatioPercent())
        .apartment(Apartment.builder().id(request.getApartmentId()).build())
        .source(WaterSource.builder().id(request.getSourceId()).build())
        .build();

    return service.create(config);
  }


  @GetMapping
  public List<ApartmentSourceConfig> getAll() {
    return service.getAll();
  }
}
