package com.management.water.modules.telemetry.repository;

import com.management.water.modules.telemetry.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    boolean existsByApartmentIdAndLogDate(Long apartmentId, LocalDate logDate);
}