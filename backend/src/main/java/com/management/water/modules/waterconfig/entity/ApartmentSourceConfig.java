package com.management.water.modules.waterconfig.entity;

import com.management.water.modules.property.entity.Apartment;
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

  @ManyToOne
  @JoinColumn(name = "apartment_id")
  private Apartment apartment;

  @ManyToOne
  @JoinColumn(name = "source_id")
  private WaterSource source;
}
