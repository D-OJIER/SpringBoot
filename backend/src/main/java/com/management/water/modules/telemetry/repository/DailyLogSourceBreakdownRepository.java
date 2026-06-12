package com.management.water.modules.telemetry.repository;

import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyLogSourceBreakdownRepository extends JpaRepository<DailyLogSourceBreakdown,Long> {
    List<DailyLogSourceBreakdown> findByDailyLogId(Long dailyLogId);
}
