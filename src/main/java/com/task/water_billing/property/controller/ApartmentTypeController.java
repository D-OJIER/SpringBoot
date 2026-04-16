package com.task.water_billing.property.controller;

import com.task.water_billing.property.entity.ApartmentType;
import com.task.water_billing.property.service.ApartmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/types")
@RequiredArgsConstructor
public class ApartmentTypeController {

    private final ApartmentTypeService service;

    @PostMapping
    public ApartmentType create(@RequestBody ApartmentType type) {
        return service.create(type);
    }
}