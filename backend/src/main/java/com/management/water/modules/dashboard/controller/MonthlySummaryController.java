package com.management.water.modules.dashboard.controller;

import com.management.water.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monthly-summary")
@RequiredArgsConstructor
public class MonthlySummaryController {

    private final DashboardService dashboardService;

    @GetMapping
        public List<Map<String, Object>> getSummary(
            @RequestParam(required = false) String apartmentNumber,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return dashboardService.getMonthlySummary(apartmentNumber, fromDate, toDate);
    }
}
