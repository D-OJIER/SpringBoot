package com.management.water.modules.dashboard.controller;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/monthly-summary")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final DailyLogRepository repository;

    @GetMapping
    public List<Map<String, Object>> getSummary() {

        List<DailyLog> logs =
                repository.findAll();

        Map<String, Map<String, Object>>
                summaryMap = new HashMap<>();

        for (DailyLog log : logs) {

            String key =
                    log.getApartment().getNumber();

            summaryMap.putIfAbsent(
                    key,
                    new HashMap<>()
            );

            Map<String, Object> summary =
                    summaryMap.get(key);

            summary.put(
                    "apartment",
                    key
            );

            double currentUsage =
                    (double) summary.getOrDefault(
                            "totalUsage",
                            0.0
                    );

            double currentCost =
                    (double) summary.getOrDefault(
                            "totalCost",
                            0.0
                    );

            summary.put(
                    "totalUsage",
                    currentUsage +
                    log.getTotalLitresConsumed()
            );

            summary.put(
                    "totalCost",
                    currentCost +
                    log.getDayCost()
            );
        }

        return new ArrayList<>(
                summaryMap.values()
        );
    }
}