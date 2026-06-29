package com.management.water.waterconfig.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
