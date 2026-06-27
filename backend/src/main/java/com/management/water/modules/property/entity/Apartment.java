package com.management.water.modules.property.entity;

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
public class Apartment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String number;

  @ManyToOne
  @JoinColumn(name = "block_id")
  private Block block;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private ApartmentType type;
}
