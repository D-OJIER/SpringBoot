package com.management.water.telemetry.integration;

import com.management.water.telemetry.avro.DailyLogAvroEvent;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAvroConsumer {

    private final DailyLogService dailyLogService;

    @KafkaListener(topics = "${integration.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeDailyLog(DailyLogAvroEvent event) {
        log.info("Received daily log event from Kafka. Date: {}, Apartment ID: {}", event.getLogDate(),
                event.getApartmentId());

        try {
            // Build the JPA Entity from the Avro event
            DailyLog logEntity = DailyLog.builder()
                    .logDate(LocalDate.parse(event.getLogDate().toString()))
                    .totalLitresConsumed(event.getTotalLitresConsumed())
                    .guestCount(event.getGuestCount())
                    .apartmentId(event.getApartmentId())
                    .build();

            // Save it using the existing business service
            dailyLogService.create(logEntity);
            log.info("Successfully persisted daily log record for date: {}", event.getLogDate());
        } catch (Exception e) {
            log.error("Failed to persist daily log from Kafka event: " + e.getMessage(), e);
        }
    }
}
