package com.management.water.security.dto;

import lombok.Data;

@Data
public class UserResponse {
  private Long id;
  private String username;
  private String role;
  private Long apartmentId;
  private String apartmentNumber;
}
