package com.task.water_billing.property.service;

import com.task.water_billing.property.entity.Block;
import com.task.water_billing.property.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository repo;

    public Block create(String name) {
        Block b = new Block();
        b.setName(name);
        return repo.save(b);
    }

    public List<Block> getAll() {
        return repo.findAll();
    }
}