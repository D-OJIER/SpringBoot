package com.management.water.modules.property.controller;

import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.service.ApartmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartment-types")
@RequiredArgsConstructor
public class ApartmentTypeController {

    private final ApartmentTypeService service;

    @PostMapping
    public ApartmentType create(@RequestBody ApartmentType type) {
        return service.create(type);
    }

    @GetMapping
    public List<ApartmentType> getAll() {
        return service.getAll();
    }
}