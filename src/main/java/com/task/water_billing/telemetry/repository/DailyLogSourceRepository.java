package com.task.water_billing.telemetry.repository;

import com.task.water_billing.telemetry.entity.DailyLogSourceBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogSourceRepository extends JpaRepository<DailyLogSourceBreakdown, Long> {}