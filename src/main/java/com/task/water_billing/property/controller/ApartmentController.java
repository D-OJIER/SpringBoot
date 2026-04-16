package com.task.water_billing.property.controller;

import com.task.water_billing.property.entity.Apartment;
import com.task.water_billing.property.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apartments")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService service;

    @PostMapping
    public Apartment create(
            @RequestParam Long blockId,
            @RequestParam Long typeId,
            @RequestParam String number) {

        return service.create(blockId, typeId, number);
    }
}