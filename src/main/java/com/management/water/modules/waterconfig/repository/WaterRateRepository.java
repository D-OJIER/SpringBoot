package com.management.water.modules.waterconfig.repository;

import com.management.water.modules.waterconfig.entity.WaterRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterRateRepository extends JpaRepository<WaterRate, Long> {
}