package com.task.water_billing.water.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "water_source")
@Getter @Setter
public class WaterSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "City Water", "Borewell"

    private String type; // e.g. "FIXED", "VARIABLE"
}