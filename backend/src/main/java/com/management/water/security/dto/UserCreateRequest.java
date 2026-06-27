package com.management.water.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  @NotNull(message = "Apartment ID is required")
  private Long apartmentId;
}
