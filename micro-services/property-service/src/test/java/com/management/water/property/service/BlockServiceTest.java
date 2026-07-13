package com.management.water.property.service;

import com.management.water.property.entity.Block;
import com.management.water.property.repository.BlockRepository;
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

public class BlockServiceTest {

    @Mock
    private BlockRepository repository;

    @InjectMocks
    private BlockService service;

    private Block block;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        block = Block.builder().id(1L).name("A").build();
    }

    @Test
    public void testCreate() {
        when(repository.save(any(Block.class))).thenReturn(block);
        Block created = service.create(block);
        assertNotNull(created);
        assertEquals("A", created.getName());
        verify(repository, times(1)).save(block);
    }

    @Test
    public void testGetAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(block));
        List<Block> list = service.getAll();
        assertEquals(1, list.size());
        assertEquals("A", list.get(0).getName());
    }

    @Test
    public void testDeleteById() {
        doNothing().when(repository).deleteById(1L);
        service.deleteById(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}
