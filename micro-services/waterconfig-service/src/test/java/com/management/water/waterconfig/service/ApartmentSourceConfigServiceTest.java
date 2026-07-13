package com.management.water.waterconfig.service;

import com.management.water.waterconfig.client.PropertyServiceClient;
import com.management.water.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.exception.ApiException;
import com.management.water.waterconfig.repository.ApartmentSourceConfigRepository;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApartmentSourceConfigServiceTest {

    @Mock
    private ApartmentSourceConfigRepository repository;

    @Mock
    private WaterSourceRepository sourceRepository;

    @Mock
    private PropertyServiceClient propertyClient;

    @InjectMocks
    private ApartmentSourceConfigService service;

    private WaterSource source;
    private ApartmentSourceConfig config;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        source = WaterSource.builder().id(1L).name("Corporation").build();
        config = ApartmentSourceConfig.builder()
                .id(10L)
                .apartmentId(100L)
                .source(source)
                .ratioPercent(50.0)
                .build();
    }

    @Test
    public void testCreate_Success() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(repository.existsByApartmentIdAndSourceId(100L, 1L)).thenReturn(false);
        when(repository.findByApartmentId(100L)).thenReturn(new ArrayList<>());
        when(repository.save(any(ApartmentSourceConfig.class))).thenReturn(config);

        ApartmentSourceConfig created = service.create(config);
        assertNotNull(created);
        verify(repository, times(1)).save(config);
    }

    @Test
    public void testCreate_ApartmentNotFound() {
        feign.FeignException.NotFound feignException = mock(feign.FeignException.NotFound.class);
        when(propertyClient.getApartmentById(100L)).thenThrow(feignException);

        assertThrows(ApiException.NotFoundException.class, () -> service.create(config));
    }

    @Test
    public void testCreate_GenericFeignError() {
        when(propertyClient.getApartmentById(100L)).thenThrow(new RuntimeException("Connection issue"));

        ApiException exception = assertThrows(ApiException.class, () -> service.create(config));
        assertEquals(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    public void testCreate_WaterSourceNotFound() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.create(config));
    }

    @Test
    public void testCreate_ConflictAlreadyExists() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(repository.existsByApartmentIdAndSourceId(100L, 1L)).thenReturn(true);

        assertThrows(ApiException.class, () -> service.create(config));
    }

    @Test
    public void testCreate_InvalidRatioPercent_ZeroOrNegative() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(repository.existsByApartmentIdAndSourceId(100L, 1L)).thenReturn(false);

        config.setRatioPercent(0.0);
        assertThrows(ApiException.class, () -> service.create(config));

        config.setRatioPercent(-10.0);
        assertThrows(ApiException.class, () -> service.create(config));
    }

    @Test
    public void testCreate_InvalidRatioPercent_GreaterThan100() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(repository.existsByApartmentIdAndSourceId(100L, 1L)).thenReturn(false);

        config.setRatioPercent(101.0);
        assertThrows(ApiException.class, () -> service.create(config));
    }

    @Test
    public void testCreate_RatioExceeds100Percent() {
        when(propertyClient.getApartmentById(100L)).thenReturn(null);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));
        when(repository.existsByApartmentIdAndSourceId(100L, 1L)).thenReturn(false);

        ApartmentSourceConfig existing = ApartmentSourceConfig.builder()
                .apartmentId(100L)
                .ratioPercent(70.0)
                .build();

        when(repository.findByApartmentId(100L)).thenReturn(Arrays.asList(existing));

        // 70 + 50 = 120 > 100
        assertThrows(ApiException.class, () -> service.create(config));
    }

    @Test
    public void testGetAllAndGetByApartmentId() {
        when(repository.findAll()).thenReturn(Collections.singletonList(config));
        when(repository.findByApartmentId(100L)).thenReturn(Collections.singletonList(config));

        List<ApartmentSourceConfig> all = service.getAll();
        assertEquals(1, all.size());

        List<ApartmentSourceConfig> byApartment = service.getByApartmentId(100L);
        assertEquals(1, byApartment.size());
    }
}
