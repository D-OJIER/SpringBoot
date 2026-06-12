package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.dto.WaterSourceCreateRequest;
import com.management.water.modules.waterconfig.service.WaterSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/water-sources")
@RequiredArgsConstructor
public class WaterSourceController {

    private final WaterSourceService service;

    @PostMapping
    public WaterSource create(@Valid @RequestBody WaterSourceCreateRequest request) {
        WaterSource source = new WaterSource();
        source.setName(request.getName());
        source.setPricingType(request.getPricingType());
        source.setSupplyType(request.getSupplyType());
        return service.create(source);
    }

    @GetMapping
    public List<WaterSource> getAll() {
        return service.getAll();
    }
}