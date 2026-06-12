package com.management.water.modules.telemetry.controller;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
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
    public Page<DailyLog> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String apartmentNumber,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.getPage(page, size, apartmentNumber, fromDate, toDate);
    }
}