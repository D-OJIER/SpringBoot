package com.management.water.telemetry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSourceDto {
  private Long id;
  private String name;
  private String pricingType;
  private String supplyType;
}
