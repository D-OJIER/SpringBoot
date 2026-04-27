package com.management.water.modules.telemetry.entity;

import com.management.water.modules.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate logDate;

    private double totalLitresConsumed;

    private int guestCount;

    private double dayCost; // will calculate later

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;
}