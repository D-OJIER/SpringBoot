package com.management.water.modules.property.controller;

import com.management.water.modules.property.entity.Block;
import com.management.water.modules.property.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService service;

    @PostMapping
    public Block create(@RequestBody Block block) {
        return service.create(block);
    }

    @GetMapping
    public List<Block> getAll() {
        return service.getAll();
    }
}