package com.task.water_billing.water.repository;

import com.task.water_billing.water.entity.WaterSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterSourceRepository extends JpaRepository<WaterSource, Long> {}