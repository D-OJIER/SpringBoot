package com.management.water.modules.property.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Example: A, B, C Block
}