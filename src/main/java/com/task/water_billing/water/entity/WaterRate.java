package com.task.water_billing.water.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "water_rate")
@Getter @Setter
public class WaterRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double minLitres;
    private double maxLitres;
    private double ratePerLitre;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private WaterSource source;
}