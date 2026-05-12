package com.management.water.modules.dashboard.controller;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DashboardController {

    private final DailyLogRepository repository;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        List<DailyLog> logs = repository.findAll();

        double totalUsage = logs.stream()
                .mapToDouble(DailyLog::getTotalLitresConsumed)
                .sum();

        double totalCost = logs.stream()
                .mapToDouble(DailyLog::getDayCost)
                .sum();

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalLogs", logs.size());
        stats.put("totalUsage", totalUsage);
        stats.put("totalCost", totalCost);

        return stats;
    }
}