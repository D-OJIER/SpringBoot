package com.task.water_billing.telemetry.entity;

import com.task.water_billing.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "daily_log")
@Getter @Setter
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