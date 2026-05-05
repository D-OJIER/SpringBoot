package com.management.water.modules.billing.repository;

import com.management.water.modules.billing.entity.SlabMonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlabMonthlySummaryRepository
        extends JpaRepository<SlabMonthlySummary, Long> {

    Optional<SlabMonthlySummary> findByApartmentIdAndSourceIdAndYearAndMonth(
            Long apartmentId,
            Long sourceId,
            int year,
            int month
    );
}