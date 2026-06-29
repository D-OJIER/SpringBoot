package com.management.water.telemetry.service;

import com.management.water.telemetry.client.WaterconfigServiceClient;
import com.management.water.telemetry.dto.ApartmentSourceConfigDto;
import com.management.water.telemetry.dto.WaterRateDto;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.telemetry.exception.ApiException;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingService {

  private final WaterconfigServiceClient waterconfigClient;
  private final DailyLogSourceBreakdownRepository breakdownRepository;

  public double calculateDailyCost(DailyLog log, double allowance) {
    double totalCost = 0;
    double totalLitres = log.getTotalLitresConsumed();

    double normalUsage = Math.min(totalLitres, allowance);
    double excessUsage = Math.max(0, totalLitres - allowance);

    List<ApartmentSourceConfigDto> configs =
        waterconfigClient.getConfigsByApartmentId(log.getApartmentId());

    for (ApartmentSourceConfigDto config : configs) {
      double ratio = config.getRatioPercent() / 100.0;

      double normalLitres = normalUsage * ratio;
      double excessLitres = excessUsage * ratio;

      Long sourceId = config.getSource().getId();

      double normalCost = calculateSlabCost(normalLitres, sourceId, log.getLogDate());
      double excessCost = calculateSlabCost(excessLitres, sourceId, log.getLogDate()) * 1.5;

      double totalSourceCost = normalCost + excessCost;
      double totalSourceLitres = normalLitres + excessLitres;

      totalCost += totalSourceCost;

      DailyLogSourceBreakdown breakdown = new DailyLogSourceBreakdown();
      breakdown.setDailyLog(log);
      breakdown.setSourceId(sourceId);
      breakdown.setLitres(totalSourceLitres);
      breakdown.setCost(totalSourceCost);

      breakdownRepository.save(breakdown);
    }

    return totalCost;
  }

  private double calculateSlabCost(double litres, Long sourceId, LocalDate date) {
    List<WaterRateDto> rates = waterconfigClient.getRatesBySourceAndDate(sourceId, date);
    if (rates.isEmpty()) {
      throw new ApiException(HttpStatus.NOT_FOUND, "No valid water rate for given date");
    }

    double remaining = litres;
    double cost = 0;

    for (WaterRateDto rate : rates) {
      if (remaining <= 0) break;

      double slabSize = rate.getMaxLitres() - rate.getMinLitres() + 1;
      double used = Math.min(remaining, slabSize);

      cost += used * rate.getRatePerLitre();
      remaining -= used;
    }

    return cost;
  }
}
