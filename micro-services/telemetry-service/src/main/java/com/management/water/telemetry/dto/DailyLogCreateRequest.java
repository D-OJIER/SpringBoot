package com.management.water.telemetry.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Data;

@Data
public class DailyLogCreateRequest {

  @NotNull(message = "Log date is required")
  @PastOrPresent(message = "Log date cannot be in the future")
  private LocalDate logDate;

  @NotNull(message = "Total litres consumed is required")
  @Min(value = 0, message = "Total litres consumed must be non-negative")
  private Double totalLitresConsumed;

  @NotNull(message = "Guest count is required")
  @Min(value = 0, message = "Guest count must be non-negative")
  private Integer guestCount;

  @NotNull(message = "Apartment ID is required")
  private Long apartmentId;
}
