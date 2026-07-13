package com.management.water.waterconfig.service;

import com.management.water.waterconfig.entity.WaterSource;
import com.management.water.waterconfig.repository.WaterSourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WaterSourceServiceTest {

    @Mock
    private WaterSourceRepository repository;

    @InjectMocks
    private WaterSourceService service;

    private WaterSource source;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        source = WaterSource.builder()
                .id(1L)
                .name("Corporation")
                .pricingType("SLAB")
                .supplyType("MUNICIPAL")
                .build();
    }

    @Test
    public void testCreate() {
        when(repository.save(any(WaterSource.class))).thenReturn(source);
        WaterSource created = service.create(source);
        assertNotNull(created);
        assertEquals("Corporation", created.getName());
        verify(repository, times(1)).save(source);
    }

    @Test
    public void testGetAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(source));
        List<WaterSource> list = service.getAll();
        assertEquals(1, list.size());
        assertEquals("Corporation", list.get(0).getName());
    }
}
