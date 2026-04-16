package com.task.water_billing.water.entity;

import com.task.water_billing.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "apartment_source_config")
@Getter @Setter
public class ApartmentSourceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double ratioPercent; // e.g. 60% city, 40% borewell
    
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private WaterSource source;
}