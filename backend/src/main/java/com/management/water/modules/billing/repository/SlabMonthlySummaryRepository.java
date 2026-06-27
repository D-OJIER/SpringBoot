package com.management.water.modules.billing.repository;

import com.management.water.modules.billing.entity.SlabMonthlySummary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlabMonthlySummaryRepository extends JpaRepository<SlabMonthlySummary, Long> {

  Optional<SlabMonthlySummary> findByApartmentIdAndSourceIdAndYearAndMonth(
      Long apartmentId, Long sourceId, int year, int month);
}
