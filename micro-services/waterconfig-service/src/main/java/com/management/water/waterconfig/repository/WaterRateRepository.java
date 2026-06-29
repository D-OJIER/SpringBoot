package com.management.water.waterconfig.repository;

import com.management.water.waterconfig.entity.WaterRate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterRateRepository extends JpaRepository<WaterRate, Long> {
  List<WaterRate> findBySourceIdOrderByMinLitresAsc(Long sourceId);

  List<WaterRate>
      findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
          Long sourceId, LocalDate date1, LocalDate date2);
}
