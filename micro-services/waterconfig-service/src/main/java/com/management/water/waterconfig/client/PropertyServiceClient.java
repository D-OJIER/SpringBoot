package com.management.water.waterconfig.client;

import com.management.water.waterconfig.config.FeignConfig;
import com.management.water.waterconfig.dto.ApartmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service", url = "${property-service.url}", configuration = FeignConfig.class)
public interface PropertyServiceClient {

  @GetMapping("/apartments/{id}")
  ApartmentDto getApartmentById(@PathVariable("id") Long id);
}
