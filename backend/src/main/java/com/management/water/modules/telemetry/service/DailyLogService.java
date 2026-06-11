package com.management.water.modules.telemetry.service;

import com.management.water.modules.billing.entity.SlabMonthlySummary;
import com.management.water.modules.billing.repository.SlabMonthlySummaryRepository;
import com.management.water.modules.billing.service.BillingService;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.modules.telemetry.repository.DailyLogRepository;
import com.management.water.modules.telemetry.repository.DailyLogSourceBreakdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final SlabMonthlySummaryRepository summaryRepository;
    private final DailyLogSourceBreakdownRepository breakdownRepository;
    private final DailyLogRepository repository;
    private final ApartmentRepository apartmentRepository;
    private final BillingService billingService;

    public DailyLog create(DailyLog log) {

        Apartment apartment = apartmentRepository.findById(log.getApartment().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Apartment not found"));

        boolean exists = repository.existsByApartmentIdAndLogDate(
                apartment.getId(),
                log.getLogDate()
        );

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "Daily log already exists for this date");
        }

        log.setApartment(apartment);

        DailyLog savedLog = repository.save(log);
        double cost = billingService.calculateDailyCost(savedLog);
        savedLog.setDayCost(cost);
        updateMonthlySummary(savedLog);
        return repository.save(savedLog);
    }
    private void updateMonthlySummary(DailyLog log) {

        int year = log.getLogDate().getYear();
        int month = log.getLogDate().getMonthValue();

        List<DailyLogSourceBreakdown> breakdowns =
                breakdownRepository.findAll()
                        .stream()
                        .filter(b -> b.getDailyLog().getId().equals(log.getId()))
                        .toList();

        for (DailyLogSourceBreakdown b : breakdowns) {

            SlabMonthlySummary summary =
                    summaryRepository
                            .findByApartmentIdAndSourceIdAndYearAndMonth(
                                    log.getApartment().getId(),
                                    b.getSource().getId(),
                                    year,
                                    month
                            )
                            .orElseGet(() -> {
                                SlabMonthlySummary s = new SlabMonthlySummary();
                                s.setApartment(log.getApartment());
                                s.setSource(b.getSource());
                                s.setYear(year);
                                s.setMonth(month);
                                s.setTotalLitres(0);
                                s.setTotalCost(0);
                                return s;
                            });

            summary.setTotalLitres(summary.getTotalLitres() + b.getLitres());
            summary.setTotalCost(summary.getTotalCost() + b.getCost());

            summaryRepository.save(summary);
        }
    }
    public List<DailyLog> getAll() {
        return repository.findAll();
    }
}