package com.management.water.modules.billing.entity;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.waterconfig.entity.WaterSource;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SlabMonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;

    private int month;

    private double totalLitres;

    private double totalCost;

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private WaterSource source;
}