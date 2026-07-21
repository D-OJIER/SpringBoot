package com.management.water.telemetry.client;

import com.management.water.telemetry.config.CacheConfig;
import com.management.water.telemetry.config.FeignConfig;
import com.management.water.telemetry.dto.ApartmentSourceConfigDto;
import com.management.water.telemetry.dto.WaterRateDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "waterconfig-service", url = "${waterconfig-service.url}", configuration = FeignConfig.class)
public interface WaterconfigServiceClient {

  @Cacheable(value = CacheConfig.APARTMENT_CONFIGS_CACHE, key = "#apartmentId")
  @GetMapping("/apartment-source-configs/apartment/{apartmentId}")
  List<ApartmentSourceConfigDto> getConfigsByApartmentId(@PathVariable("apartmentId") Long apartmentId);

  @Cacheable(value = CacheConfig.WATER_RATES_CACHE, key = "#sourceId + '-' + #date")
  @GetMapping("/water-rates/source/{sourceId}/date/{date}")
  List<WaterRateDto> getRatesBySourceAndDate(
      @PathVariable("sourceId") Long sourceId,
      @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}

