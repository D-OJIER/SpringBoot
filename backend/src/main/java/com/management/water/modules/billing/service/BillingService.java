package com.management.water.modules.billing.service;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.modules.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final ApartmentSourceConfigRepository configRepository;
    private final WaterRateRepository rateRepository;
    private final DailyLogSourceBreakdownRepository breakdownRepository;

    public double calculateDailyCost(DailyLog log) {

        double totalCost = 0;

        double allowance = calculateAllowance(log);

        double totalLitres = log.getTotalLitresConsumed();

        double normalUsage = Math.min(totalLitres, allowance);
        double excessUsage = Math.max(0, totalLitres - allowance);

        List<ApartmentSourceConfig> configs =
                configRepository.findByApartmentId(log.getApartment().getId());

        for (ApartmentSourceConfig config : configs) {

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
            breakdown.setSource(config.getSource());
            breakdown.setLitres(totalSourceLitres);
            breakdown.setCost(totalSourceCost);

            breakdownRepository.save(breakdown);
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
            throw new ApiException(HttpStatus.NOT_FOUND, "No valid water rate for given date");
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

    private double calculateAllowance(DailyLog log) {

        int base = log.getApartment().getType().getBaseOccupancy();
        int guests = log.getGuestCount();

        double litresPerPerson =
                log.getApartment().getType().getLitresPerPerson();

        return (base + guests) * litresPerPerson;
    }
}