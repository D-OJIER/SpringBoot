package com.task.water_billing.telemetry.repository;

import com.task.water_billing.telemetry.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByApartmentId(Long apartmentId);
    
}
