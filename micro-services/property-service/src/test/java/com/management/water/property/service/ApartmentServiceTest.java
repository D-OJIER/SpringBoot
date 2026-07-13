package com.management.water.property.service;

import com.management.water.property.entity.Apartment;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.entity.Block;
import com.management.water.property.exception.ApiException;
import com.management.water.property.repository.ApartmentRepository;
import com.management.water.property.repository.ApartmentTypeRepository;
import com.management.water.property.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApartmentServiceTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ApartmentTypeRepository typeRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    private Block block;
    private ApartmentType type;
    private Apartment apartment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        block = Block.builder().id(1L).name("A").build();
        type = ApartmentType.builder().id(2L).name("2BHK").build();
        apartment = Apartment.builder()
                .id(10L)
                .number("A-101")
                .block(block)
                .type(type)
                .build();
    }

    @Test
    public void testCreate_Success() {
        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(typeRepository.findById(2L)).thenReturn(Optional.of(type));
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(apartment);

        Apartment created = apartmentService.create(apartment);

        assertNotNull(created);
        assertEquals("A-101", created.getNumber());
        assertEquals("A", created.getBlock().getName());
        assertEquals("2BHK", created.getType().getName());
        verify(apartmentRepository, times(1)).save(apartment);
    }

    @Test
    public void testCreate_BlockNotFound() {
        when(blockRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> apartmentService.create(apartment));
        verify(apartmentRepository, never()).save(any());
    }

    @Test
    public void testCreate_TypeNotFound() {
        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(typeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> apartmentService.create(apartment));
        verify(apartmentRepository, never()).save(any());
    }

    @Test
    public void testGetAll() {
        when(apartmentRepository.findAll()).thenReturn(Arrays.asList(apartment));

        List<Apartment> list = apartmentService.getAll();
        assertEquals(1, list.size());
        assertEquals("A-101", list.get(0).getNumber());
    }

    @Test
    public void testGetById_Success() {
        when(apartmentRepository.findById(10L)).thenReturn(Optional.of(apartment));

        Apartment found = apartmentService.getById(10L);
        assertNotNull(found);
        assertEquals("A-101", found.getNumber());
    }

    @Test
    public void testGetById_NotFound() {
        when(apartmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ApiException.NotFoundException.class, () -> apartmentService.getById(99L));
    }
}
