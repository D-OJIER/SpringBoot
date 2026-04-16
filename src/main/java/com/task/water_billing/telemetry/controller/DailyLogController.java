package com.task.water_billing.telemetry.controller;

import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService service;

    @PostMapping
    public DailyLog create(@RequestParam Long apartmentId, @RequestParam double litres) {
        return service.create(apartmentId, litres);
    }
}