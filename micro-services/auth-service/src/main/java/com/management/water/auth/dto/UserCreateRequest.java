package com.management.water.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  /**
   * The apartment that will be linked to this resident user.
   * auth-service stores apartmentId as a plain FK — it does NOT validate
   * the apartment exists. When property-service is up, validation can be
   * added via a Feign call in UserService.
   */
  @NotNull(message = "Apartment ID is required")
  private Long apartmentId;
}
