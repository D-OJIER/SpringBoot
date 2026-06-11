package com.management.water.modules.dashboard.service;

import com.management.water.modules.telemetry.entity.DailyLog;
import com.management.water.modules.telemetry.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DailyLogService dailyLogService;

    public Map<String, Object> getStats() {
        List<DailyLog> logs = dailyLogService.getAll();

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

    public List<Map<String, Object>> getMonthlySummary() {
        List<DailyLog> logs = dailyLogService.getAll();
        Map<String, Map<String, Object>> summaryMap = new HashMap<>();

        for (DailyLog log : logs) {
            String key = log.getApartment().getNumber();
            summaryMap.putIfAbsent(key, new HashMap<>());
            Map<String, Object> summary = summaryMap.get(key);

            summary.put("apartment", key);

            double currentUsage = (double) summary.getOrDefault("totalUsage", 0.0);
            double currentCost = (double) summary.getOrDefault("totalCost", 0.0);

            summary.put("totalUsage", currentUsage + log.getTotalLitresConsumed());
            summary.put("totalCost", currentCost + log.getDayCost());
        }

        return new ArrayList<>(summaryMap.values());
    }
}
