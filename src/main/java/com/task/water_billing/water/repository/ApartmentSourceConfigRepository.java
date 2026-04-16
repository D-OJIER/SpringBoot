package com.task.water_billing.water.repository;

import com.task.water_billing.water.entity.ApartmentSourceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentSourceConfigRepository extends JpaRepository<ApartmentSourceConfig, Long> {

    List<ApartmentSourceConfig> findByApartmentId(Long apartmentId);
}