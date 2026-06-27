package com.management.water.modules.telemetry.entity;

import com.management.water.modules.waterconfig.entity.WaterSource;
import jakarta.persistence.*;
import lombok.Data;

@Entity
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

  @ManyToOne
  @JoinColumn(name = "source_id")
  private WaterSource source;
}
