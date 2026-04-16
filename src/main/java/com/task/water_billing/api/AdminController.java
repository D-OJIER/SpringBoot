package com.task.water_billing.api;

import com.task.water_billing.property.repository.ApartmentRepository;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import com.task.water_billing.telemetry.repository.DailyLogSourceBreakdownRepository;
import com.task.water_billing.water.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ApartmentRepository apartmentRepo;
    private final DailyLogRepository logRepo;
    private final WaterSourceRepository sourceRepo;
    private final WaterRateRepository rateRepo;
    private final ApartmentSourceConfigRepository configRepo;
    private final DailyLogSourceBreakdownRepository breakdownRepo;

    @GetMapping("/logs")
    public Object getAllLogs() {
        return logRepo.findAll();
    }

    @GetMapping("/breakdown/{logId}")
    public Object getBreakdown(@PathVariable Long logId) {
        return breakdownRepo.findByDailyLogId(logId);
    }

    @GetMapping("/apartments")
    public Object getApartments() {
        return apartmentRepo.findAll();
    }

    @GetMapping("/sources")
    public Object getSources() {
        return sourceRepo.findAll();
    }

    @DeleteMapping("/configs")
    public String clearConfigs() {
        configRepo.deleteAll();
        return "All configs cleared";
    }
}