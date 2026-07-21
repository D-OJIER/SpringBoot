package com.management.water.waterconfig.service;

import com.management.water.waterconfig.dto.event.WaterConfigEvent;
import com.management.water.waterconfig.entity.WaterRate;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.exception.ApiException;
import com.management.water.waterconfig.repository.WaterRateRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class WaterRateService {

  private final WaterRateRepository rateRepository;
  private final WaterSourceRepository sourceRepository;
  private final KafkaTemplate<String, WaterConfigEvent> kafkaTemplate;

  @Value("${app.kafka.topics.water-config:water-config-events}")
  private String topicName;

  public WaterRate create(WaterRate rate) {
    WaterSource source =
        sourceRepository
            .findById(rate.getSource().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

    rate.setSource(source);

    if (rate.getMinLitres() > rate.getMaxLitres()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
    }

    if (rate.getEffectiveFrom() == null) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date is required");
    }

    if (rate.getEffectiveFrom().isAfter(LocalDate.now())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date cannot be in the future");
    }

    if (rate.getEffectiveTo() != null && rate.getEffectiveTo().isBefore(rate.getEffectiveFrom())) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST, "Effective to date must be on or after effective from date");
    }

    validateSlabs(source.getId(), rate);

    WaterRate saved = rateRepository.save(rate);
    publishEvent("RATE_CREATED", saved.getId(), saved.getSource().getId(), null);
    return saved;
  }

  private void validateSlabs(Long sourceId, WaterRate candidate) {
    List<WaterRate> rates = rateRepository.findBySourceIdOrderByMinLitresAsc(sourceId);

    List<WaterRate> allRates = new ArrayList<>(rates);
    allRates.add(candidate);
    allRates.sort((a, b) -> Double.compare(a.getMinLitres(), b.getMinLitres()));

    if (allRates.isEmpty()) return;

    if (allRates.get(0).getMinLitres() != 0) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST, "The first water rate slab must start at 0 litres");
    }

    for (int i = 0; i < allRates.size() - 1; i++) {
      WaterRate current = allRates.get(i);
      WaterRate next = allRates.get(i + 1);

      if (current.getMaxLitres() + 1 != next.getMinLitres()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs are not continuous");
      }

      if (current.getMaxLitres() >= next.getMinLitres()) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs overlap");
      }
    }
  }

  public void deleteById(Long id) {
    WaterRate rate =
        rateRepository
            .findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));
    if (rate.getEffectiveFrom().isBefore(LocalDate.now())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete historical rates");
    }

    Long sourceId = rate.getSource() != null ? rate.getSource().getId() : null;
    rateRepository.deleteById(id);
    publishEvent("RATE_DELETED", id, sourceId, null);
  }

  public List<WaterRate> getAll() {
    return rateRepository.findAll();
  }

  public List<WaterRate> getRatesBySourceAndDate(Long sourceId, LocalDate date) {
    return rateRepository
        .findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
            sourceId, date, date);
  }

  public WaterRate update(Long id, WaterRate updated) {
    WaterRate existing =
        rateRepository
            .findById(id)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));

    WaterSource source =
        sourceRepository
            .findById(updated.getSource().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

    if (updated.getMinLitres() > updated.getMaxLitres()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
    }

    if (updated.getEffectiveFrom() == null) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date is required");
    }

    if (updated.getEffectiveFrom().isAfter(LocalDate.now())) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Effective from date cannot be in the future");
    }

    if (updated.getEffectiveTo() != null
        && updated.getEffectiveTo().isBefore(updated.getEffectiveFrom())) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST, "Effective to date must be on or after effective from date");
    }

    List<WaterRate> rates = rateRepository.findBySourceIdOrderByMinLitresAsc(source.getId());

    for (WaterRate rate : rates) {
      if (rate.getId().equals(id)) {
        continue;
      }

      boolean overlap =
          updated.getMinLitres() <= rate.getMaxLitres()
              && updated.getMaxLitres() >= rate.getMinLitres();

      if (overlap) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "Slab overlaps existing slabs");
      }
    }

    existing.setMinLitres(updated.getMinLitres());
    existing.setMaxLitres(updated.getMaxLitres());
    existing.setRatePerLitre(updated.getRatePerLitre());
    existing.setEffectiveFrom(updated.getEffectiveFrom());
    existing.setEffectiveTo(updated.getEffectiveTo());
    existing.setSource(source);

    WaterRate saved = rateRepository.save(existing);
    publishEvent("RATE_UPDATED", saved.getId(), saved.getSource().getId(), null);
    return saved;
  }

  private void publishEvent(String eventType, Long entityId, Long sourceId, Long apartmentId) {
    try {
      WaterConfigEvent event = WaterConfigEvent.builder()
          .eventType(eventType)
          .entityType("WATER_RATE")
          .entityId(entityId)
          .sourceId(sourceId)
          .apartmentId(apartmentId)
          .build();
      kafkaTemplate.send(topicName, String.valueOf(sourceId), event);
      log.info("Published WaterConfigEvent [{}] to Kafka topic {}", eventType, topicName);
    } catch (Exception e) {
      log.error("Failed to publish WaterConfigEvent to Kafka: {}", e.getMessage(), e);
    }
  }
}

