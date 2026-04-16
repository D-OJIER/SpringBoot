package com.task.water_billing.water.controller;

import com.task.water_billing.water.entity.WaterRate;
import com.task.water_billing.water.service.WaterRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class WaterRateController {

    private final WaterRateService service;

    @PostMapping
    public WaterRate create(@RequestParam Long sourceId, @RequestBody WaterRate rate) {
        return service.create(sourceId, rate);
    }
}