package com.management.water.modules.property.repository;

import com.management.water.modules.property.entity.ApartmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentTypeRepository extends JpaRepository<ApartmentType, Long> {
}