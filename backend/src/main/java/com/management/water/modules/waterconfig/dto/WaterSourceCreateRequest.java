package com.management.water.modules.waterconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WaterSourceCreateRequest {

  @NotBlank(message = "Water source name is required")
  private String name;

  @NotBlank(message = "Pricing type is required")
  @Pattern(regexp = "SLAB|FIXED", message = "Pricing type must be either SLAB or FIXED")
  private String pricingType;

  @NotBlank(message = "Supply type is required")
  @Pattern(
      regexp = "MUNICIPAL|PRIVATE",
      message = "Supply type must be either MUNICIPAL or PRIVATE")
  private String supplyType;
}
