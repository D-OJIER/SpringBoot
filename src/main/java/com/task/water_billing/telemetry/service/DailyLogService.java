package com.task.water_billing.telemetry.service;

import com.task.water_billing.billing.service.BillingService;
import com.task.water_billing.property.repository.ApartmentRepository;
import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final DailyLogRepository repo;
    private final ApartmentRepository apartmentRepo;
    private final BillingService billingService;

    public DailyLog create(Long apartmentId, double litres) {

        var apartment = apartmentRepo.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        DailyLog log = new DailyLog();
        log.setApartment(apartment);
        log.setLogDate(LocalDate.now());
        log.setTotalLitresConsumed(litres);
        log.setGuestCount(0);

        log = repo.save(log);

        billingService.processDailyLog(log.getId());

        return log;
    }
}