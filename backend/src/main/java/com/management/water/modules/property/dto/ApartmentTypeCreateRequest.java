package com.management.water.modules.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApartmentTypeCreateRequest {

  @NotBlank(message = "Apartment type name is required")
  private String name;

  @NotNull(message = "Base occupancy is required")
  @Min(value = 1, message = "Base occupancy must be at least 1")
  private Integer baseOccupancy;

  @NotNull(message = "Litres per person is required")
  @Min(value = 0, message = "Litres per person must be non-negative")
  private Double litresPerPerson;
}
