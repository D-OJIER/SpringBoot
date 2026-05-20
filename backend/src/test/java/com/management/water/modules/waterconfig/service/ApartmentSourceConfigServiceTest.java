package com.management.water.modules.waterconfig.service;


import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApartmentSourceConfigServiceTest {

    @Mock
    private ApartmentSourceConfigRepository repository;

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private WaterSourceRepository sourceRepository;

    @InjectMocks
    private ApartmentSourceConfigService service;

    @Test
    void shouldCreateConfigurationSuccessfully() {

        Apartment apartment = new Apartment();
        apartment.setId(1L);

        WaterSource source = new WaterSource();
        source.setId(1L);

        ApartmentSourceConfig config = new ApartmentSourceConfig();
        config.setApartment(apartment);
        config.setSource(source);
        config.setRatioPercent(40.0);

        when(apartmentRepository.findById(1L))
                .thenReturn(Optional.of(apartment));

        when(sourceRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(repository.existsByApartmentIdAndSourceId(1L, 1L))
                .thenReturn(false);

        when(repository.findByApartmentId(1L))
                .thenReturn(List.of());

        when(repository.save(any(ApartmentSourceConfig.class)))
                .thenReturn(config);

        ApartmentSourceConfig result = service.create(config);

        assertNotNull(result);

        assertEquals(40.0, result.getRatioPercent());

        verify(repository).save(config);
    }

    @Test
    void shouldThrowExceptionWhenApartmentNotFound() {

        Apartment apartment = new Apartment();
        apartment.setId(1L);

        WaterSource source = new WaterSource();
        source.setId(1L);

        ApartmentSourceConfig config = new ApartmentSourceConfig();
        config.setApartment(apartment);
        config.setSource(source);

        when(apartmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.create(config)
        );

        assertEquals(
                "Apartment not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenSourceAlreadyExists() {

        Apartment apartment = new Apartment();
        apartment.setId(1L);

        WaterSource source = new WaterSource();
        source.setId(1L);

        ApartmentSourceConfig config = new ApartmentSourceConfig();
        config.setApartment(apartment);
        config.setSource(source);

        when(apartmentRepository.findById(1L))
                .thenReturn(Optional.of(apartment));

        when(sourceRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(repository.existsByApartmentIdAndSourceId(1L, 1L))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.create(config)
        );

        assertEquals(
                "Source already configured for this apartment",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenRatioIsInvalid() {

        Apartment apartment = new Apartment();
        apartment.setId(1L);

        WaterSource source = new WaterSource();
        source.setId(1L);

        ApartmentSourceConfig config = new ApartmentSourceConfig();
        config.setApartment(apartment);
        config.setSource(source);
        config.setRatioPercent(150.0);

        when(apartmentRepository.findById(1L))
                .thenReturn(Optional.of(apartment));

        when(sourceRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(repository.existsByApartmentIdAndSourceId(1L, 1L))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.create(config)
        );

        assertEquals(
                "Invalid ratio value",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalRatioExceeds100() {

        Apartment apartment = new Apartment();
        apartment.setId(1L);

        WaterSource source = new WaterSource();
        source.setId(1L);

        ApartmentSourceConfig existingConfig = new ApartmentSourceConfig();
        existingConfig.setRatioPercent(70.0);

        ApartmentSourceConfig newConfig = new ApartmentSourceConfig();
        newConfig.setApartment(apartment);
        newConfig.setSource(source);
        newConfig.setRatioPercent(40.0);

        when(apartmentRepository.findById(1L))
                .thenReturn(Optional.of(apartment));

        when(sourceRepository.findById(1L))
                .thenReturn(Optional.of(source));

        when(repository.existsByApartmentIdAndSourceId(1L, 1L))
                .thenReturn(false);

        when(repository.findByApartmentId(1L))
                .thenReturn(List.of(existingConfig));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.create(newConfig)
        );

        assertEquals(
                "Total ratio exceeds 100%",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnAllConfigurations() {

        ApartmentSourceConfig config = new ApartmentSourceConfig();

        when(repository.findAll())
                .thenReturn(List.of(config));

        List<ApartmentSourceConfig> result = service.getAll();

        assertEquals(1, result.size());

        verify(repository).findAll();
    }
}