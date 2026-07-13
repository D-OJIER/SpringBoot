package com.management.water.telemetry.service;

import com.management.water.telemetry.client.WaterconfigServiceClient;
import com.management.water.telemetry.dto.ApartmentSourceConfigDto;
import com.management.water.telemetry.dto.WaterRateDto;
import com.management.water.telemetry.dto.WaterSourceDto;
import com.management.water.telemetry.entity.DailyLog;
import com.management.water.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.telemetry.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BillingServiceTest {

    @Mock
    private WaterconfigServiceClient waterconfigClient;

    @Mock
    private DailyLogSourceBreakdownRepository breakdownRepository;

    @InjectMocks
    private BillingService billingService;

    private DailyLog log;
    private ApartmentSourceConfigDto configDto;
    private WaterRateDto rateDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        log = DailyLog.builder()
                .apartmentId(100L)
                .totalLitresConsumed(300.0)
                .logDate(LocalDate.now())
                .build();

        WaterSourceDto sourceDto = new WaterSourceDto();
        sourceDto.setId(1L);
        sourceDto.setName("Corporation");

        configDto = new ApartmentSourceConfigDto();
        configDto.setApartmentId(100L);
        configDto.setSource(sourceDto);
        configDto.setRatioPercent(100.0);

        rateDto = new WaterRateDto();
        rateDto.setId(10L);
        rateDto.setMinLitres(0.0);
        rateDto.setMaxLitres(1000.0);
        rateDto.setRatePerLitre(5.0);
    }

    @Test
    public void testCalculateDailyCost_NoExcess() {
        when(waterconfigClient.getConfigsByApartmentId(100L)).thenReturn(Arrays.asList(configDto));
        when(waterconfigClient.getRatesBySourceAndDate(eq(1L), any(LocalDate.class)))
                .thenReturn(Arrays.asList(rateDto));

        double cost = billingService.calculateDailyCost(log, 400.0);

        assertEquals(1500.0, cost, 0.01);
        verify(breakdownRepository, times(1)).save(any());
    }

    @Test
    public void testCalculateDailyCost_WithExcessPenalty() {
        when(waterconfigClient.getConfigsByApartmentId(100L)).thenReturn(Arrays.asList(configDto));
        when(waterconfigClient.getRatesBySourceAndDate(eq(1L), any(LocalDate.class)))
                .thenReturn(Arrays.asList(rateDto));

        double cost = billingService.calculateDailyCost(log, 200.0);

        // Normal: 200 * 5.0 = 1000.0
        // Excess: 100 * 7.5 = 750.0
        assertEquals(1750.0, cost, 0.01);
    }

    @Test
    public void testCalculateDailyCost_NoRatesFound() {
        when(waterconfigClient.getConfigsByApartmentId(100L)).thenReturn(Arrays.asList(configDto));
        when(waterconfigClient.getRatesBySourceAndDate(eq(1L), any(LocalDate.class)))
                .thenReturn(java.util.Collections.emptyList());

        assertThrows(ApiException.class, () -> billingService.calculateDailyCost(log, 200.0));
    }

    @Test
    public void testCalculateDailyCost_NoConfigsFound() {
        when(waterconfigClient.getConfigsByApartmentId(100L)).thenReturn(java.util.Collections.emptyList());

        double cost = billingService.calculateDailyCost(log, 200.0);

        assertEquals(0.0, cost, 0.01);
    }
}
