package com.management.water.waterconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

@Data
public class WaterRateCreateRequest {

  @NotNull(message = "Minimum litres is required")
  @Min(value = 0, message = "Minimum litres must be non-negative")
  private Double minLitres;

  @NotNull(message = "Maximum litres is required")
  @Min(value = 0, message = "Maximum litres must be non-negative")
  private Double maxLitres;

  @NotNull(message = "Rate per litre is required")
  @Min(value = 0, message = "Rate per litre must be non-negative")
  private Double ratePerLitre;

  @NotNull(message = "Effective from date is required")
  @PastOrPresent(message = "Effective from date cannot be in the future")
  private LocalDate effectiveFrom;

  private LocalDate effectiveTo;

  @NotNull(message = "Water source ID is required")
  private Long sourceId;

  @JsonProperty("source")
  private void unpackNestedSource(Map<String, Object> source) {
    if (source != null && source.containsKey("id") && source.get("id") != null) {
      this.sourceId = Long.valueOf(source.get("id").toString());
    }
  }
}
