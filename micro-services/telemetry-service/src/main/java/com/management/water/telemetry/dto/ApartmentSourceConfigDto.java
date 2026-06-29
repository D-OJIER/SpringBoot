package com.management.water.telemetry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentSourceConfigDto {
  private Long id;
  private double ratioPercent;
  private Long apartmentId;
  private WaterSourceDto source;
}
