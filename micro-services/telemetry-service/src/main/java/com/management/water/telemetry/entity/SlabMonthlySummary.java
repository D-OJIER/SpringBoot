package com.management.water.telemetry.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "slab_monthly_summary")
@Data
public class SlabMonthlySummary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int year;

  private int month;

  @Column(name = "total_litres")
  private double totalLitres;

  @Column(name = "total_cost")
  private double totalCost;

  @Column(name = "apartment_id")
  private Long apartmentId;

  @Column(name = "source_id")
  private Long sourceId;
}
