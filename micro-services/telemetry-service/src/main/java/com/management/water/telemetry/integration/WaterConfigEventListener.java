package com.management.water.telemetry.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.water.telemetry.config.CacheConfig;
import com.management.water.telemetry.dto.event.WaterConfigEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaterConfigEventListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "${integration.kafka.config-topic:water-config-events}",
        groupId = "${integration.kafka.config-group:telemetry-config-group}"
    )
    public void handleWaterConfigEvent(String rawMessage) {
        log.info("Received WaterConfigEvent from Kafka: {}", rawMessage);

        try {
            WaterConfigEvent event = objectMapper.readValue(rawMessage, WaterConfigEvent.class);

            if ("WATER_RATE".equalsIgnoreCase(event.getEntityType())) {
                Cache ratesCache = cacheManager.getCache(CacheConfig.WATER_RATES_CACHE);
                if (ratesCache != null) {
                    ratesCache.clear();
                    log.info("Evicted 'waterRates' cache due to event [{}] for sourceId: {}",
                            event.getEventType(), event.getSourceId());
                }
            } else if ("APARTMENT_SOURCE_CONFIG".equalsIgnoreCase(event.getEntityType())) {
                Cache configCache = cacheManager.getCache(CacheConfig.APARTMENT_CONFIGS_CACHE);
                if (configCache != null) {
                    if (event.getApartmentId() != null) {
                        configCache.evict(event.getApartmentId());
                        log.info("Evicted 'apartmentConfigs' cache entry for apartmentId: {}", event.getApartmentId());
                    } else {
                        configCache.clear();
                        log.info("Cleared entire 'apartmentConfigs' cache due to event [{}]", event.getEventType());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to process WaterConfigEvent payload: " + e.getMessage(), e);
        }
    }
}
