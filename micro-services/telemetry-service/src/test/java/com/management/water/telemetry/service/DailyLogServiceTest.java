package com.management.water.telemetry.service;

import com.management.water.telemetry.client.AuthServiceClient;
import com.management.water.telemetry.client.PropertyServiceClient;
import com.management.water.telemetry.dto.ApartmentDto;
import com.management.water.telemetry.dto.ApartmentTypeDto;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.exception.ApiException;
import com.management.water.telemetry.repository.DailyLogRepository;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.telemetry.repository.SlabMonthlySummaryRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DailyLogServiceTest {

    @Mock private SlabMonthlySummaryRepository summaryRepository;
    @Mock private DailyLogSourceBreakdownRepository breakdownRepository;
    @Mock private DailyLogRepository repository;
    @Mock private PropertyServiceClient propertyClient;
    @Mock private AuthServiceClient authClient;
    @Mock private BillingService billingService;

    private DailyLogService service;
    private DailyLog log;
    private ApartmentDto apartment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new DailyLogService(
                summaryRepository,
                breakdownRepository,
                repository,
                propertyClient,
                authClient,
                billingService,
                new SimpleMeterRegistry()
        );

        log = DailyLog.builder()
                .id(10L)
                .apartmentId(100L)
                .logDate(LocalDate.now().minusDays(1))
                .totalLitresConsumed(300.0)
                .guestCount(2)
                .build();

        ApartmentTypeDto typeDto = new ApartmentTypeDto();
        typeDto.setId(2L);
        typeDto.setBaseOccupancy(2);
        typeDto.setLitresPerPerson(100.0);

        apartment = new ApartmentDto();
        apartment.setId(100L);
        apartment.setNumber("A-101");
        apartment.setType(typeDto);
    }

    @Test
    public void testCreate_Success() {
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(false);
        when(repository.save(any(DailyLog.class))).thenReturn(log);
        when(billingService.calculateDailyCost(any(DailyLog.class), anyDouble())).thenReturn(25.0);

        DailyLog created = service.create(log);
        assertNotNull(created);
        assertEquals("A-101", created.getApartmentNumber());
        assertEquals(25.0, created.getDayCost());
        verify(repository, times(2)).save(any(DailyLog.class));
    }

    @Test
    public void testCreate_ApartmentNotFound() {
        // Mock Feign Not Found
        feign.FeignException.NotFound feignException = mock(feign.FeignException.NotFound.class);
        when(propertyClient.getApartmentById(100L)).thenThrow(feignException);

        assertThrows(ApiException.class, () -> service.create(log));
    }

    @Test
    public void testCreate_DuplicateLogForDate() {
        when(propertyClient.getApartmentById(100L)).thenReturn(apartment);
        when(repository.existsByApartmentIdAndLogDate(100L, log.getLogDate())).thenReturn(true);

        assertThrows(ApiException.class, () -> service.create(log));
    }
}
