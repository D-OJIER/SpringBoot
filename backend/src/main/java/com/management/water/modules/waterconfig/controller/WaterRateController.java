package com.management.water.modules.waterconfig.controller;

import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.service.WaterRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/water-rates")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class WaterRateController {

    private final WaterRateService service;

    @PostMapping
    public WaterRate create(@RequestBody WaterRate rate) {
        return service.create(rate);
    }

    @GetMapping
    public List<WaterRate> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PutMapping("/{id}")
    public WaterRate update(@PathVariable Long id, @RequestBody WaterRate updated) {

        return service.update(id, updated);
    }
}