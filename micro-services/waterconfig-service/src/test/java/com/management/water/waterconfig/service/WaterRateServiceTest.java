package com.management.water.waterconfig.service;

import com.management.water.waterconfig.entity.WaterRate;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.exception.ApiException;
import com.management.water.waterconfig.repository.WaterRateRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WaterRateServiceTest {

    @Mock
    private WaterRateRepository rateRepository;

    @Mock
    private WaterSourceRepository sourceRepository;

    @InjectMocks
    private WaterRateService rateService;

    private WaterSource source;
    private WaterRate rate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        source = WaterSource.builder().id(1L).name("Corporation").build();
        rate = WaterRate.builder()
                .id(10L)
                .source(source)
                .minLitres(0.0)
                .maxLitres(100.0)
                .ratePerLitre(5.0)
                .effectiveFrom(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    public void testCreate_Success() {
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(new ArrayList<>());
        when(rateRepository.save(any(WaterRate.class))).thenReturn(rate);

        WaterRate created = rateService.create(rate);
        assertNotNull(created);
        verify(rateRepository, times(1)).save(rate);
    }

    @Test
    public void testCreate_SourceNotFound() {
        when(sourceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testCreate_FirstSlabNotZero() {
        rate.setMinLitres(10.0);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(new ArrayList<>());

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testCreate_InvalidMinMaxRange() {
        rate.setMinLitres(100.0);
        rate.setMaxLitres(50.0);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testCreate_FutureEffectiveDate() {
        rate.setEffectiveFrom(LocalDate.now().plusDays(5));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testDeleteById_HistoricalRate() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate)); // effectiveFrom is in past
        assertThrows(ApiException.class, () -> rateService.deleteById(10L));
    }
}
