package com.management.water.modules.waterconfig.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.management.water.modules.common.exception.ApiException;
import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WaterRateServiceTest {

  @Mock private WaterRateRepository rateRepository;

  @Mock private WaterSourceRepository sourceRepository;

  @InjectMocks private WaterRateService service;

  private WaterSource source;

  @BeforeEach
  void setUp() {
    source = new WaterSource();
    source.setId(1L);
  }

  @Test
  void shouldCreateWaterRateSuccessfully() {
    WaterRate rate = new WaterRate();
    rate.setMinLitres(0);
    rate.setMaxLitres(100);
    rate.setRatePerLitre(2.5);
    rate.setEffectiveFrom(LocalDate.now().minusDays(1));
    rate.setSource(source);

    when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
    when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(List.of());
    when(rateRepository.save(any(WaterRate.class))).thenReturn(rate);

    WaterRate result = service.create(rate);

    assertNotNull(result);
    assertEquals(0, result.getMinLitres());
    assertEquals(100, result.getMaxLitres());
  }

  @Test
  void shouldThrowWhenSlabRangeInvalid() {
    WaterRate rate = new WaterRate();
    rate.setMinLitres(200);
    rate.setMaxLitres(100);
    rate.setRatePerLitre(2.5);
    rate.setEffectiveFrom(LocalDate.now().minusDays(1));
    rate.setSource(source);

    when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

    ApiException exception = assertThrows(ApiException.class, () -> service.create(rate));

    assertEquals("Invalid slab range", exception.getClientMessage());
  }

  @Test
  void shouldThrowWhenRateSlabsOverlapOnUpdate() {
    WaterRate existing = new WaterRate();
    existing.setId(2L);
    existing.setMinLitres(0);
    existing.setMaxLitres(100);
    existing.setSource(source);

    WaterRate updated = new WaterRate();
    updated.setMinLitres(50);
    updated.setMaxLitres(150);
    updated.setRatePerLitre(3.0);
    updated.setEffectiveFrom(LocalDate.now().minusDays(1));
    updated.setSource(source);

    when(rateRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
    when(rateRepository.findBySourceIdOrderByMinLitresAsc(1L)).thenReturn(List.of(existing));

    ApiException exception = assertThrows(ApiException.class, () -> service.update(1L, updated));

    assertEquals("Slab overlaps existing slabs", exception.getClientMessage());
  }

  @Test
  void shouldThrowWhenDeletingHistoricalRate() {
    WaterRate existing = new WaterRate();
    existing.setId(1L);
    existing.setEffectiveFrom(LocalDate.now().minusDays(10));
    existing.setSource(source);

    when(rateRepository.findById(1L)).thenReturn(Optional.of(existing));

    ApiException exception = assertThrows(ApiException.class, () -> service.deleteById(1L));

    assertEquals("Cannot delete historical rates", exception.getClientMessage());
  }
}
