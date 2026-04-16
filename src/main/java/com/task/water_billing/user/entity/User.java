package com.task.water_billing.user.entity;

import com.task.water_billing.property.entity.Apartment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "users")
@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ADMIN / USER

    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;
}