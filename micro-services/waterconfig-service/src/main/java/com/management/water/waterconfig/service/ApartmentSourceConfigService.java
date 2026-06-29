package com.management.water.waterconfig.service;

import com.management.water.waterconfig.client.PropertyServiceClient;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.exception.ApiException;
import com.management.water.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApartmentSourceConfigService {

  private final ApartmentSourceConfigRepository repository;
  private final WaterSourceRepository sourceRepository;
  private final PropertyServiceClient propertyClient;

  public ApartmentSourceConfig create(ApartmentSourceConfig config) {
    // Validate apartment existence via Feign client
    try {
      propertyClient.getApartmentById(config.getApartmentId());
    } catch (feign.FeignException.NotFound e) {
      throw new ApiException.NotFoundException("Apartment not found");
    } catch (Exception e) {
      throw new ApiException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error validating apartment: " + e.getMessage());
    }

    WaterSource source =
        sourceRepository
            .findById(config.getSource().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

    boolean exists = repository.existsByApartmentIdAndSourceId(config.getApartmentId(), source.getId());

    if (exists) {
      throw new ApiException(HttpStatus.CONFLICT, "Source already configured for this apartment");
    }

    if (config.getRatioPercent() <= 0 || config.getRatioPercent() > 100) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid ratio value");
    }

    List<ApartmentSourceConfig> existingConfigs = repository.findByApartmentId(config.getApartmentId());

    double currentTotal =
        existingConfigs.stream().mapToDouble(ApartmentSourceConfig::getRatioPercent).sum();

    double newTotal = currentTotal + config.getRatioPercent();

    if (newTotal > 100) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Total ratio exceeds 100%");
    }

    config.setSource(source);

    return repository.save(config);
  }

  public List<ApartmentSourceConfig> getAll() {
    return repository.findAll();
  }

  public List<ApartmentSourceConfig> getByApartmentId(Long apartmentId) {
    return repository.findByApartmentId(apartmentId);
  }
}
