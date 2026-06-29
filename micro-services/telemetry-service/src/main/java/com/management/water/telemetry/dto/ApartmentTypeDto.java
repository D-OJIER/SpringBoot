package com.management.water.telemetry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentTypeDto {
  private Long id;
  private String name;
  private int baseOccupancy;
  private double litresPerPerson;
}
