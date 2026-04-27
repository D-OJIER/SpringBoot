package com.management.water.modules.property.service;

import com.management.water.modules.property.entity.Block;
import com.management.water.modules.property.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository repository;

    public Block create(Block block) {
        return repository.save(block);
    }

    public List<Block> getAll() {
        return repository.findAll();
    }
}