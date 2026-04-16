package com.task.water_billing.billing.service;

import com.task.water_billing.billing.calculator.SlabCalculator;
import com.task.water_billing.property.entity.Apartment;
import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.entity.DailyLogSourceBreakdown;
import com.task.water_billing.telemetry.entity.GuestStay;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import com.task.water_billing.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.task.water_billing.telemetry.repository.GuestStayRepository;
import com.task.water_billing.water.entity.ApartmentSourceConfig;
import com.task.water_billing.water.entity.WaterRate;
import com.task.water_billing.water.repository.ApartmentSourceConfigRepository;
import com.task.water_billing.water.repository.WaterRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final GuestStayRepository guestRepo;
    private final DailyLogRepository logRepo;
    private final ApartmentSourceConfigRepository configRepo;
    private final WaterRateRepository rateRepo;
    private final DailyLogSourceBreakdownRepository breakdownRepo;

    private final SlabCalculator calculator = new SlabCalculator();

    private int getGuestCount(Long apartmentId, LocalDate date) {
        return guestRepo.findByApartmentId(apartmentId).stream()
                .filter(g ->
                        (g.getCheckInDate().isBefore(date) || g.getCheckInDate().isEqual(date)) &&
                                (g.getCheckOutDate().isAfter(date) || g.getCheckOutDate().isEqual(date))
                )
                .mapToInt(GuestStay::getGuestCount)
                .sum();
    }

    public void processDailyLog(Long logId) {

        DailyLog dailyLog = logRepo.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));

        Apartment apartment = dailyLog.getApartment();
        LocalDate logDate = dailyLog.getLogDate();

        int guestCount = getGuestCount(apartment.getId(), logDate);

        int baseOccupancy = apartment.getType().getBaseOccupancy();
        int litresPerPerson = apartment.getType().getLitresPerPerson();

        int effectivePeople = baseOccupancy + guestCount;
        double allowedWater = effectivePeople * litresPerPerson;

        log.info("Base occupancy: {}", baseOccupancy);
        log.info("Guest count (on {}): {}", logDate, guestCount);
        log.info("Allowed water: {}", allowedWater);

        List<ApartmentSourceConfig> configs =
                configRepo.findByApartmentId(apartment.getId());

        if (configs.isEmpty()) {
            throw new RuntimeException("No source config found for apartment");
        }

        double totalRatio = configs.stream()
                .mapToDouble(ApartmentSourceConfig::getRatioPercent)
                .sum();

        if (Math.abs(totalRatio - 100) > 0.01) {
            throw new RuntimeException("Invalid ratio configuration. Must equal 100%");
        }

        double totalUsage = dailyLog.getTotalLitresConsumed();

        double chargeableUsage = Math.max(0, totalUsage - allowedWater);

        log.info("Total usage: {}", totalUsage);
        log.info("Chargeable usage after allowance: {}", chargeableUsage);

        BigDecimal totalCost = BigDecimal.ZERO;

        breakdownRepo.deleteAll(
                breakdownRepo.findByDailyLogId(dailyLog.getId())
        );

        for (ApartmentSourceConfig config : configs) {

            double splitUsage = chargeableUsage * (config.getRatioPercent() / 100);

            log.info("Source: {} | Ratio: {}% | Usage Split: {}",
                    config.getSource().getName(),
                    config.getRatioPercent(),
                    splitUsage);

            List<WaterRate> slabs =
                    rateRepo.findBySourceId(config.getSource().getId());

            slabs = slabs.stream()
                    .sorted(Comparator.comparingDouble(WaterRate::getMinLitres))
                    .toList();

            BigDecimal cost = calculator.calculate(slabs, splitUsage);

            log.info("Cost for source {} = {}",
                    config.getSource().getName(),
                    cost);

            totalCost = totalCost.add(cost);

            DailyLogSourceBreakdown breakdown = new DailyLogSourceBreakdown();
            breakdown.setDailyLog(dailyLog);
            breakdown.setSource(config.getSource());
            breakdown.setLitres(splitUsage);
            breakdown.setCost(cost.doubleValue());

            breakdownRepo.save(breakdown);
        }

        log.info("Final total cost = {}", totalCost);

        dailyLog.setDayCost(totalCost.doubleValue());
        logRepo.save(dailyLog);
    }
}