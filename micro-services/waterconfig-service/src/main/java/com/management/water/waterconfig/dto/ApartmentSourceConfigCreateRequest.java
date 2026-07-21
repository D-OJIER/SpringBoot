package com.management.water.waterconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;

@Data
public class ApartmentSourceConfigCreateRequest {

  @NotNull(message = "Ratio percentage is required")
  @Min(value = 0, message = "Ratio percentage must be between 0 and 100")
  @Max(value = 100, message = "Ratio percentage must be between 0 and 100")
  private Double ratioPercent;

  @NotNull(message = "Apartment ID is required")
  private Long apartmentId;

  @NotNull(message = "Water source ID is required")
  private Long sourceId;

  @JsonProperty("source")
  private void unpackNestedSource(Map<String, Object> source) {
    if (source != null && source.containsKey("id") && source.get("id") != null) {
      this.sourceId = Long.valueOf(source.get("id").toString());
    }
  }
}

