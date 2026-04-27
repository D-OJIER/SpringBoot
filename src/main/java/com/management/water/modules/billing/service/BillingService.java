package com.management.water.modules.billing.service;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final ApartmentSourceConfigRepository configRepository;
    private final WaterRateRepository rateRepository;

    public double calculateDailyCost(DailyLog log) {

        double totalCost = 0;

        List<ApartmentSourceConfig> configs =
                configRepository.findByApartmentId(log.getApartment().getId());

        for (ApartmentSourceConfig config : configs) {

            double ratio = config.getRatioPercent() / 100.0;

            double litresForSource = log.getTotalLitresConsumed() * ratio;

            double cost = calculateSlabCost(
                    litresForSource,
                    config.getSource().getId(),
                    log.getLogDate()
            );

            totalCost += cost;
        }

        return totalCost;
    }

    private double calculateSlabCost(double litres, Long sourceId, LocalDate date) {

        List<WaterRate> rates =
                rateRepository
                        .findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
                                sourceId,
                                date,
                                date
                        );
        if (rates.isEmpty()) {
            throw new RuntimeException("No valid water rate for given date");
        }

        double remaining = litres;
        double cost = 0;

        for (WaterRate rate : rates) {

            if (remaining <= 0) break;

            double slabSize = rate.getMaxLitres() - rate.getMinLitres() + 1;

            double used = Math.min(remaining, slabSize);

            cost += used * rate.getRatePerLitre();

            remaining -= used;
        }

        return cost;
    }


}