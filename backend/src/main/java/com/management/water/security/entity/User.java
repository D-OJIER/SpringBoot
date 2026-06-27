package com.management.water.security.entity;

import com.management.water.modules.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @ManyToOne
  @JoinColumn(name = "apartment_id")
  private Apartment apartment;
}
