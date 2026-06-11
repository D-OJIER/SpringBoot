package com.management.water.modules.waterconfig.service;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentSourceConfigService {

    private final ApartmentSourceConfigRepository repository;
    private final ApartmentRepository apartmentRepository;
    private final WaterSourceRepository sourceRepository;

    public ApartmentSourceConfig create(ApartmentSourceConfig config) {

        Apartment apartment = apartmentRepository.findById(config.getApartment().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Apartment not found"));

        WaterSource source = sourceRepository.findById(config.getSource().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

        boolean exists = repository.existsByApartmentIdAndSourceId(
                apartment.getId(),
                source.getId()
        );

        if (exists) {
            throw new ApiException(HttpStatus.CONFLICT, "Source already configured for this apartment");
        }

        if (config.getRatioPercent() <= 0 || config.getRatioPercent() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid ratio value");
        }

        List<ApartmentSourceConfig> existingConfigs =
                repository.findByApartmentId(apartment.getId());

        double currentTotal = existingConfigs.stream()
                .mapToDouble(ApartmentSourceConfig::getRatioPercent)
                .sum();

        double newTotal = currentTotal + config.getRatioPercent();

        if (newTotal > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Total ratio exceeds 100%");
        }

        config.setApartment(apartment);
        config.setSource(source);

        return repository.save(config);
    }

    public List<ApartmentSourceConfig> getAll() {
        return repository.findAll();
    }
}