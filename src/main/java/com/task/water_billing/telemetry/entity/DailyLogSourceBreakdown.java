package com.task.water_billing.telemetry.entity;

import com.task.water_billing.water.entity.WaterSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "daily_log_source")
@Getter @Setter
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