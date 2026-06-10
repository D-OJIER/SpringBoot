package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.service.WaterSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/water-sources")
@RequiredArgsConstructor
public class WaterSourceController {

    private final WaterSourceService service;

    @PostMapping
    public WaterSource create(@RequestBody WaterSource source) {
        return service.create(source);
    }

    @GetMapping
    public List<WaterSource> getAll() {
        return service.getAll();
    }
}