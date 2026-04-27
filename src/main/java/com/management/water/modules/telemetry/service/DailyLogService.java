package com.management.water.modules.telemetry.service;

import com.management.water.modules.billing.service.BillingService;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyLogService {

    private final DailyLogRepository repository;
    private final ApartmentRepository apartmentRepository;
    private final BillingService billingService;

    public DailyLog create(DailyLog log) {

        Apartment apartment = apartmentRepository.findById(log.getApartment().getId())
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        boolean exists = repository.existsByApartmentIdAndLogDate(
                apartment.getId(),
                log.getLogDate()
        );

        if (exists) {
            throw new RuntimeException("Daily log already exists for this date");
        }

        log.setApartment(apartment);

        double cost = billingService.calculateDailyCost(log);
        log.setDayCost(cost);

        return repository.save(log);
    }

    public List<DailyLog> getAll() {
        return repository.findAll();
    }
}