package com.management.water.telemetry.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "daily_log",
    indexes = {
      @Index(name = "idx_daily_log_date", columnList = "log_date"),
      @Index(name = "idx_daily_log_apartment_date", columnList = "apartment_id, log_date")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "log_date")
  private LocalDate logDate;

  @Column(name = "total_litres_consumed")
  private double totalLitresConsumed;

  @Column(name = "guest_count")
  private int guestCount;

  @Column(name = "day_cost")
  private double dayCost;

  @Column(name = "apartment_id")
  private Long apartmentId;

  @Column(name = "apartment_number")
  private String apartmentNumber;
}
