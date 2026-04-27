package com.management.water.modules.property.repository;

import com.management.water.modules.property.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
}