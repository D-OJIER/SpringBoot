package com.task.water_billing.telemetry.repository;

import com.task.water_billing.telemetry.entity.DailyLogSourceBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyLogSourceBreakdownRepository extends JpaRepository<DailyLogSourceBreakdown, Long> {

    List<DailyLogSourceBreakdown> findByDailyLogId(Long logId);
}