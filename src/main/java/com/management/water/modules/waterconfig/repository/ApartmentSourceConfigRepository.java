package com.management.water.modules.waterconfig.repository;

import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentSourceConfigRepository extends JpaRepository<ApartmentSourceConfig, Long> {
    List<ApartmentSourceConfig> findByApartmentId(Long apartmentId);
    boolean existsByApartmentIdAndSourceId(Long apartmentId, Long sourceId);
}