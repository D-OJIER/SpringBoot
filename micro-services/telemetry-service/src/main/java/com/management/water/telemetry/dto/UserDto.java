package com.management.water.telemetry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private Long id;
  private String username;
  private String role;
  private Long apartmentId;
}
