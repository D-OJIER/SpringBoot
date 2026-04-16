package com.task.water_billing.water.controller;

import com.task.water_billing.water.entity.ApartmentSourceConfig;
import com.task.water_billing.water.service.ApartmentSourceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/source-config")
@RequiredArgsConstructor
public class ApartmentSourceConfigController {

    private final ApartmentSourceConfigService service;

    @PostMapping
    public ApartmentSourceConfig create(
            @RequestParam Long apartmentId,
            @RequestParam Long sourceId,
            @RequestParam double ratio) {

        return service.create(apartmentId, sourceId, ratio);
    }
}