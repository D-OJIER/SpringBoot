package com.management.water.modules.waterconfig.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class WaterSource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name; // City, Borewell, Tanker

  private String pricingType; // SLAB, FIXED

  private String supplyType; // MUNICIPAL, PRIVATE
}
