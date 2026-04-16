package com.task.water_billing.property.repository;

import com.task.water_billing.property.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {}