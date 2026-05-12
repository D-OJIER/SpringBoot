package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.waterconfig.entity.ApartmentSourceConfig;
import com.management.water.modules.waterconfig.service.ApartmentSourceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartment-source-configs")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ApartmentSourceConfigController {

    private final ApartmentSourceConfigService service;

    @PostMapping
    public ApartmentSourceConfig create(@RequestBody ApartmentSourceConfig config) {
        return service.create(config);
    }

    @GetMapping
    public List<ApartmentSourceConfig> getAll() {
        return service.getAll();
    }
}