package com.management.water.modules.telemetry.controller;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService service;

    @PostMapping
    public DailyLog create(@RequestBody DailyLog log) {
        return service.create(log);
    }

    @GetMapping
    public List<DailyLog> getAll() {
        return service.getAll();
    }
}