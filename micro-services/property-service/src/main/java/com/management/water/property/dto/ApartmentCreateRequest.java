package com.management.water.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApartmentCreateRequest {

  @NotBlank(message = "Apartment number is required")
  private String number;

  @NotNull(message = "Block ID is required")
  private Long blockId;

  @NotNull(message = "Apartment type ID is required")
  private Long typeId;
}
