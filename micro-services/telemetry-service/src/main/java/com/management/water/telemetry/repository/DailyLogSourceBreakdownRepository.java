package com.management.water.telemetry.repository;

import com.management.water.telemetry.entity.DailyLogSourceBreakdown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyLogSourceBreakdownRepository
    extends JpaRepository<DailyLogSourceBreakdown, Long> {
  List<DailyLogSourceBreakdown> findByDailyLogId(Long dailyLogId);
}
