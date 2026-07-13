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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    public void testCreate_InvalidMinMaxRange() {
        rate.setMinLitres(100.0);
        rate.setMaxLitres(50.0);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testCreate_EffectiveFromNull() {
        rate.setEffectiveFrom(null);
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
    public void testCreate_EffectiveToBeforeEffectiveFrom() {
        rate.setEffectiveTo(LocalDate.now().minusDays(5));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

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
    public void testCreate_SlabsNotContinuous() {
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate existing = WaterRate.builder()
                .id(9L)
                .minLitres(0.0)
                .maxLitres(50.0)
                .build();
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(Arrays.asList(existing));

        // existing ends at 50.0, candidate starts at 100.0 (non-continuous)
        rate.setMinLitres(100.0);
        rate.setMaxLitres(200.0);

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testCreate_SlabsOverlap() {
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate existing = WaterRate.builder()
                .id(9L)
                .minLitres(0.0)
                .maxLitres(150.0)
                .build();
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(Arrays.asList(existing));

        // existing ends at 150.0, candidate starts at 100.0 (overlaps)
        rate.setMinLitres(100.0);
        rate.setMaxLitres(200.0);

        assertThrows(ApiException.class, () -> rateService.create(rate));
    }

    @Test
    public void testDeleteById_Success() {
        rate.setEffectiveFrom(LocalDate.now().plusDays(1)); // future, not historical
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));

        rateService.deleteById(10L);
        verify(rateRepository, times(1)).deleteById(10L);
    }

    @Test
    public void testDeleteById_NotFound() {
        when(rateRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> rateService.deleteById(10L));
    }

    @Test
    public void testDeleteById_HistoricalRate() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate)); // effectiveFrom is in past
        assertThrows(ApiException.class, () -> rateService.deleteById(10L));
    }

    @Test
    public void testGetAllAndGetRatesBySourceAndDate() {
        when(rateRepository.findAll()).thenReturn(Collections.singletonList(rate));
        when(rateRepository.findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(rate));

        List<WaterRate> all = rateService.getAll();
        assertEquals(1, all.size());

        List<WaterRate> rates = rateService.getRatesBySourceAndDate(1L, LocalDate.now());
        assertEquals(1, rates.size());
    }

    @Test
    public void testUpdate_Success() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(new ArrayList<>());
        when(rateRepository.save(any(WaterRate.class))).thenReturn(rate);

        WaterRate updated = WaterRate.builder()
                .source(source)
                .minLitres(0.0)
                .maxLitres(150.0)
                .ratePerLitre(6.0)
                .effectiveFrom(LocalDate.now().minusDays(1))
                .build();

        WaterRate result = rateService.update(10L, updated);
        assertNotNull(result);
        assertEquals(150.0, result.getMaxLitres());
        assertEquals(6.0, result.getRatePerLitre());
    }

    @Test
    public void testUpdate_NotFound() {
        when(rateRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> rateService.update(10L, rate));
    }

    @Test
    public void testUpdate_SourceNotFound() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> rateService.update(10L, rate));
    }

    @Test
    public void testUpdate_InvalidMinMaxRange() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate updated = WaterRate.builder()
                .source(source)
                .minLitres(100.0)
                .maxLitres(50.0)
                .build();

        assertThrows(ApiException.class, () -> rateService.update(10L, updated));
    }

    @Test
    public void testUpdate_EffectiveFromNull() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate updated = WaterRate.builder()
                .source(source)
                .effectiveFrom(null)
                .build();

        assertThrows(ApiException.class, () -> rateService.update(10L, updated));
    }

    @Test
    public void testUpdate_EffectiveFromInFuture() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate updated = WaterRate.builder()
                .source(source)
                .effectiveFrom(LocalDate.now().plusDays(2))
                .build();

        assertThrows(ApiException.class, () -> rateService.update(10L, updated));
    }

    @Test
    public void testUpdate_EffectiveToBeforeEffectiveFrom() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate updated = WaterRate.builder()
                .source(source)
                .effectiveFrom(LocalDate.now().minusDays(2))
                .effectiveTo(LocalDate.now().minusDays(5))
                .build();

        assertThrows(ApiException.class, () -> rateService.update(10L, updated));
    }

    @Test
    public void testUpdate_SlabOverlaps() {
        when(rateRepository.findById(10L)).thenReturn(Optional.of(rate));
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        WaterRate existingRate = WaterRate.builder()
                .id(20L)
                .minLitres(101.0)
                .maxLitres(200.0)
                .build();
        when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(Arrays.asList(rate, existingRate));

        WaterRate updated = WaterRate.builder()
                .source(source)
                .minLitres(0.0)
                .maxLitres(150.0) // overlapping with 101.0-200.0 rate
                .effectiveFrom(LocalDate.now().minusDays(1))
                .build();

        assertThrows(ApiException.class, () -> rateService.update(10L, updated));
    }
}
