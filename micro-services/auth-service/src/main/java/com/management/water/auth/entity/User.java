package com.management.water.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * User entity for auth-service.
 *
 * <p>Key change from monolith: The @ManyToOne Apartment FK has been replaced with a plain
 * Long apartmentId. The auth-service does NOT own the apartment data — it stores only the
 * foreign key reference. Apartment details are fetched from property-service via REST when needed.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  /**
   * Stores the apartment reference as a plain ID.
   * Property-service owns the Apartment aggregate — auth-service only holds the FK.
   */
  @Column(name = "apartment_id")
  private Long apartmentId;
}
