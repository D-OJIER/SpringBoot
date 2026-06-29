package com.management.water.telemetry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDto {
  private Long id;
  private String number;
  private BlockDto block;
  private ApartmentTypeDto type;
}
