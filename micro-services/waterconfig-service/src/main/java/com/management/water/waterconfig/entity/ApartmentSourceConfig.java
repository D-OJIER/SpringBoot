package com.management.water.waterconfig.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentSourceConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double ratioPercent; // Example: 60, 40

  private Long apartmentId;

  @ManyToOne
  @JoinColumn(name = "source_id")
  private WaterSource source;
}
