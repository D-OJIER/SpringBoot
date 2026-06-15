package com.management.water.modules.waterconfig.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
public class WaterRate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double minLitres;

  private double maxLitres;

  private double ratePerLitre;

  private LocalDate effectiveFrom;

  private LocalDate effectiveTo;

  @ManyToOne
  @JoinColumn(name = "source_id")
  private WaterSource source;
}
