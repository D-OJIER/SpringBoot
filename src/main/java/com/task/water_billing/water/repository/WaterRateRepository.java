package com.task.water_billing.water.repository;

import com.task.water_billing.water.entity.WaterRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterRateRepository extends JpaRepository<WaterRate, Long> {

    List<WaterRate> findBySourceId(Long sourceId);}