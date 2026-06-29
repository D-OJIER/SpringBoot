package com.management.water.telemetry.repository;

import com.management.water.telemetry.entity.SlabMonthlySummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlabMonthlySummaryRepository extends JpaRepository<SlabMonthlySummary, Long> {
  Optional<SlabMonthlySummary> findByApartmentIdAndSourceIdAndYearAndMonth(
      Long apartmentId, Long sourceId, int year, int month);
}
