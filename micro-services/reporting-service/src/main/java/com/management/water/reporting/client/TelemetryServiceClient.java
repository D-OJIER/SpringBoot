package com.management.water.reporting.client;

import com.management.water.reporting.config.FeignConfig;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "telemetry-service", url = "${telemetry-service.url}", configuration = FeignConfig.class)
public interface TelemetryServiceClient {

  @GetMapping("/dashboard/stats")
  Map<String, Object> getStats(
      @RequestParam(name = "apartmentNumber", required = false) String apartmentNumber,
      @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) LocalDate toDate);

  @GetMapping("/monthly-summary")
  List<Map<String, Object>> getSummary(
      @RequestParam(name = "apartmentNumber", required = false) String apartmentNumber,
      @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) LocalDate toDate);
}
