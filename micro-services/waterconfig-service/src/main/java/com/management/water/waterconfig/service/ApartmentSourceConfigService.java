package com.management.water.waterconfig.service;

import com.management.water.waterconfig.client.PropertyServiceClient;
import com.management.water.waterconfig.dto.event.WaterConfigEvent;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.exception.ApiException;
import com.management.water.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApartmentSourceConfigService {

  private final ApartmentSourceConfigRepository repository;
  private final WaterSourceRepository sourceRepository;
  private final PropertyServiceClient propertyClient;
  private final KafkaTemplate<String, WaterConfigEvent> kafkaTemplate;

  @Value("${app.kafka.topics.water-config:water-config-events}")
  private String topicName;

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

    ApartmentSourceConfig saved = repository.save(config);
    publishEvent("CONFIG_CREATED", saved.getId(), saved.getSource().getId(), saved.getApartmentId());
    return saved;
  }

  public List<ApartmentSourceConfig> getAll() {
    return repository.findAll();
  }

  public List<ApartmentSourceConfig> getByApartmentId(Long apartmentId) {
    return repository.findByApartmentId(apartmentId);
  }

  private void publishEvent(String eventType, Long entityId, Long sourceId, Long apartmentId) {
    try {
      WaterConfigEvent event = WaterConfigEvent.builder()
          .eventType(eventType)
          .entityType("APARTMENT_SOURCE_CONFIG")
          .entityId(entityId)
          .sourceId(sourceId)
          .apartmentId(apartmentId)
          .build();
      kafkaTemplate.send(topicName, String.valueOf(apartmentId), event);
      log.info("Published WaterConfigEvent [{}] for apartment {} to Kafka topic {}", eventType, apartmentId, topicName);
    } catch (Exception e) {
      log.error("Failed to publish WaterConfigEvent to Kafka: {}", e.getMessage(), e);
    }
  }
}

