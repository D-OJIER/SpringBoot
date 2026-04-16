package com.task.water_billing.water.controller;

import com.task.water_billing.water.entity.WaterSource;
import com.task.water_billing.water.service.WaterSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sources")
@RequiredArgsConstructor
public class WaterSourceController {

    private final WaterSourceService service;

    @PostMapping
    public WaterSource create(@RequestBody WaterSource source) {
        return service.create(source);
    }
}