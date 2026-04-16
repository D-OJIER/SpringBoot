package com.task.water_billing.property.controller;

import com.task.water_billing.property.entity.Block;
import com.task.water_billing.property.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService service;

    @PostMapping
    public Block create(@RequestParam String name) {
        return service.create(name);
    }

    @GetMapping
    public List<Block> getAll() {
        return service.getAll();
    }
}
