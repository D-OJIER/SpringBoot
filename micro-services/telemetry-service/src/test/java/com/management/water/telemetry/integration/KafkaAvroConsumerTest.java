package com.management.water.telemetry.integration;

import com.management.water.telemetry.avro.DailyLogAvroEvent;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.service.DailyLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class KafkaAvroConsumerTest {

    @Mock
    private DailyLogService dailyLogService;

    @InjectMocks
    private KafkaAvroConsumer consumer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConsumeDailyLog() {
        DailyLogAvroEvent event = DailyLogAvroEvent.newBuilder()
                .setApartmentId(100L)
                .setGuestCount(2)
                .setLogDate("2026-07-10")
                .setTotalLitresConsumed(350.0)
                .build();

        consumer.consumeDailyLog(event);

        ArgumentCaptor<DailyLog> argumentCaptor = ArgumentCaptor.forClass(DailyLog.class);
        verify(dailyLogService, times(1)).create(argumentCaptor.capture());

        DailyLog capturedLog = argumentCaptor.getValue();
        assertEquals(100L, capturedLog.getApartmentId());
        assertEquals(2, capturedLog.getGuestCount());
        assertEquals(LocalDate.of(2026, 7, 10), capturedLog.getLogDate());
        assertEquals(350.0, capturedLog.getTotalLitresConsumed());
    }
}
