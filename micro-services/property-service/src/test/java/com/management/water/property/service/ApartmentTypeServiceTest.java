package com.management.water.property.service;

import com.management.water.property.entity.ApartmentType;
import com.management.water.property.repository.ApartmentTypeRepository;
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

public class ApartmentTypeServiceTest {

    @Mock
    private ApartmentTypeRepository repository;

    @InjectMocks
    private ApartmentTypeService service;

    private ApartmentType type;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        type = ApartmentType.builder().id(2L).name("2BHK").build();
    }

    @Test
    public void testCreate() {
        when(repository.save(any(ApartmentType.class))).thenReturn(type);
        ApartmentType created = service.create(type);
        assertNotNull(created);
        assertEquals("2BHK", created.getName());
        verify(repository, times(1)).save(type);
    }

    @Test
    public void testGetAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(type));
        List<ApartmentType> list = service.getAll();
        assertEquals(1, list.size());
        assertEquals("2BHK", list.get(0).getName());
    }
}
