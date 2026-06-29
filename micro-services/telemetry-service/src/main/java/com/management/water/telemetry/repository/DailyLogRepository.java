package com.management.water.telemetry.repository;

import com.management.water.telemetry.entity.DailyLog;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyLogRepository
    extends JpaRepository<DailyLog, Long>, JpaSpecificationExecutor<DailyLog> {
  boolean existsByApartmentIdAndLogDate(Long apartmentId, LocalDate logDate);

  @Query(
      """
            SELECT COUNT(log) AS totalLogs,
                   COALESCE(SUM(log.totalLitresConsumed), 0) AS totalUsage,
                   COALESCE(SUM(log.dayCost), 0) AS totalCost
            FROM DailyLog log
            WHERE log.logDate BETWEEN :fromDate AND :toDate
              AND (:apartmentId IS NULL OR log.apartmentId = :apartmentId)
              AND (:apartmentNumber IS NULL OR LOWER(log.apartmentNumber) LIKE LOWER(CONCAT('%', :apartmentNumber, '%')))
            """)
  DashboardStatsProjection summarizeStats(
      @Param("apartmentId") Long apartmentId,
      @Param("apartmentNumber") String apartmentNumber,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate);

  @Query(
      """
            SELECT log.apartmentNumber AS apartment,
                   COALESCE(SUM(log.totalLitresConsumed), 0) AS totalUsage,
                   COALESCE(SUM(log.dayCost), 0) AS totalCost
            FROM DailyLog log
            WHERE log.logDate BETWEEN :fromDate AND :toDate
              AND (:apartmentId IS NULL OR log.apartmentId = :apartmentId)
              AND (:apartmentNumber IS NULL OR LOWER(log.apartmentNumber) LIKE LOWER(CONCAT('%', :apartmentNumber, '%')))
            GROUP BY log.apartmentNumber
            ORDER BY log.apartmentNumber
            """)
  List<MonthlySummaryProjection> summarizeByApartment(
      @Param("apartmentId") Long apartmentId,
      @Param("apartmentNumber") String apartmentNumber,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate);

  interface DashboardStatsProjection {
    long getTotalLogs();

    double getTotalUsage();

    double getTotalCost();
  }

  interface MonthlySummaryProjection {
    String getApartment();

    double getTotalUsage();

    double getTotalCost();
  }
}
