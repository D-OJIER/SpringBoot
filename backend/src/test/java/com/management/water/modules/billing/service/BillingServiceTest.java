package com.management.water.modules.billing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.entity.DailyLogSourceBreakdown;
import com.management.water.modules.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

  @Mock private ApartmentSourceConfigRepository configRepository;

  @Mock private WaterRateRepository rateRepository;

  @Mock private DailyLogSourceBreakdownRepository breakdownRepository;

  @InjectMocks private BillingService billingService;

  private DailyLog dailyLog;

  private WaterSource source;

  @BeforeEach
  void setup() {

    ApartmentType apartmentType = ApartmentType.builder()
        .baseOccupancy(4)
        .litresPerPerson(135.0)
        .build();

    Apartment apartment = Apartment.builder()
        .id(1L)
        .type(apartmentType)
        .build();

    dailyLog = DailyLog.builder()
        .apartment(apartment)
        .guestCount(1)
        .totalLitresConsumed(1000.0)
        .logDate(LocalDate.now())
        .build();

    source = WaterSource.builder()
        .id(1L)
        .build();
  }

  @Test
  void shouldCalculateDailyCostSuccessfully() {

    ApartmentSourceConfig config = ApartmentSourceConfig.builder()
        .ratioPercent(100.0)
        .source(source)
        .build();

    when(configRepository.findByApartmentId(1L)).thenReturn(List.of(config));

    WaterRate rate = WaterRate.builder()
        .minLitres(0)
        .maxLitres(2000)
        .ratePerLitre(2.0)
        .build();

    when(rateRepository
            .findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(rate));

    double result = billingService.calculateDailyCost(dailyLog);

    /*
       allowance:
       (4 + 1) * 135 = 675

       normal = 675
       excess = 325

       normal cost = 675 * 2 = 1350
       excess cost = 325 * 2 * 1.5 = 975

       total = 2325
    */

    assertEquals(2325.0, result);

    verify(configRepository).findByApartmentId(1L);

    verify(rateRepository, times(2))
        .findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
            eq(1L), any(LocalDate.class), any(LocalDate.class));

    verify(breakdownRepository).save(any(DailyLogSourceBreakdown.class));
  }

  @Test
  void shouldThrowExceptionWhenNoRatesFound() {

    ApartmentSourceConfig config = ApartmentSourceConfig.builder()
        .ratioPercent(100.0)
        .source(source)
        .build();

    when(configRepository.findByApartmentId(1L)).thenReturn(List.of(config));

    when(rateRepository
            .findBySourceIdAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqualOrderByMinLitresAsc(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());

    RuntimeException exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, () -> billingService.calculateDailyCost(dailyLog));

    assertEquals("No valid water rate for given date", exception.getMessage());
  }
}
