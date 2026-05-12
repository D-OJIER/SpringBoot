package com.management.water.modules.property.controller;

import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService service;

    @PostMapping
    public Apartment create(@RequestBody Apartment apartment) {
        return service.create(apartment);
    }

    @GetMapping
    public List<Apartment> getAll() {
        return service.getAll();
    }

}