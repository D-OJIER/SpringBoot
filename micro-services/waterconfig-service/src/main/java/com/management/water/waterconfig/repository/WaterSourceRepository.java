package com.management.water.waterconfig.repository;

import com.management.water.waterconfig.entity.WaterSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterSourceRepository extends JpaRepository<WaterSource, Long> {
}
