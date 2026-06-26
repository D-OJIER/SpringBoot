package com.management.water.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApartmentTypeCreateRequest {

  @NotBlank(message = "Apartment type name is required")
  private String name;

  @Min(value = 1, message = "Base occupancy must be at least 1")
  private int baseOccupancy;

  @Positive(message = "Litres per person must be a positive number")
  private double litresPerPerson;
}
