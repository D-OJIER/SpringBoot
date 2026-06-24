package com.management.water.modules.telemetry.entity;

import com.management.water.modules.property.entity.Apartment;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(
    indexes = {
      @Index(name = "idx_daily_log_date", columnList = "log_date"),
      @Index(name = "idx_daily_log_apartment_date", columnList = "apartment_id, log_date")
    })
@Data
public class DailyLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate logDate;

  private double totalLitresConsumed;

  private int guestCount;

  private double dayCost;

  @ManyToOne
  @JoinColumn(name = "apartment_id")
  private Apartment apartment;
}
