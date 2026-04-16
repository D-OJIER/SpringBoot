package com.task.water_billing.water.service;

import com.task.water_billing.property.repository.ApartmentRepository;
import com.task.water_billing.water.entity.ApartmentSourceConfig;
import com.task.water_billing.water.repository.ApartmentSourceConfigRepository;
import com.task.water_billing.water.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApartmentSourceConfigService {

    private final ApartmentSourceConfigRepository repo;
    private final ApartmentRepository apartmentRepo;
    private final WaterSourceRepository sourceRepo;

    public ApartmentSourceConfig create(Long apartmentId, Long sourceId, double ratio) {

        var apartment = apartmentRepo.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        var source = sourceRepo.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Source not found"));

        ApartmentSourceConfig config = new ApartmentSourceConfig();
        config.setApartment(apartment);
        config.setSource(source);
        config.setRatioPercent(ratio);

        return repo.save(config);
    }
}