package com.task.water_billing.property.repository;

import com.task.water_billing.property.entity.ApartmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentTypeRepository extends JpaRepository<ApartmentType, Long> {}