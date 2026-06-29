package com.management.water.telemetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "daily_log_source_breakdown")
@Data
public class DailyLogSourceBreakdown {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double litres;

  private double cost;

  @ManyToOne
  @JoinColumn(name = "daily_log_id")
  private DailyLog dailyLog;

  @Column(name = "source_id")
  private Long sourceId;
}
