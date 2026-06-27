package com.management.water.modules.telemetry.repository;

import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogSourceBreakdownRepository
    extends JpaRepository<DailyLogSourceBreakdown, Long> {
  List<DailyLogSourceBreakdown> findByDailyLogId(Long dailyLogId);
}
