package com.management.water.telemetry.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterRateDto {
  private Long id;
  private double minLitres;
  private double maxLitres;
  private double ratePerLitre;
  private LocalDate effectiveFrom;
  private LocalDate effectiveTo;
  private WaterSourceDto source;
}
