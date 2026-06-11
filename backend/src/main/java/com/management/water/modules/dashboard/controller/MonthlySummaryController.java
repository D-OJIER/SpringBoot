package com.management.water.modules.dashboard.controller;

import com.management.water.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monthly-summary")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final DashboardService dashboardService;

    @GetMapping
    public List<Map<String, Object>> getSummary() {
        return dashboardService.getMonthlySummary();
    }
}
