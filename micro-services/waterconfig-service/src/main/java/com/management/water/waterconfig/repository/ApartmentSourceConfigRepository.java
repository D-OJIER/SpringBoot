package com.management.water.waterconfig.repository;

import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentSourceConfigRepository
    extends JpaRepository<ApartmentSourceConfig, Long> {
  List<ApartmentSourceConfig> findByApartmentId(Long apartmentId);

  boolean existsByApartmentIdAndSourceId(Long apartmentId, Long sourceId);
}
