package com.task.water_billing.billing.scheduler;

import com.task.water_billing.billing.service.BillingService;
import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final DailyLogRepository logRepo;
    private final BillingService billingService;

    //@Scheduled(cron = "0 0 0 * * ?")
    //For testing
    @Scheduled(fixedRate = 20000)
    public void runDailyBilling() {

        log.info("❌✅ Running scheduled billing job...");

        List<DailyLog> logs = logRepo.findAll();

        for (DailyLog l : logs) {

            try {
                billingService.processDailyLog(l.getId());
                log.info("✅ Processed log ID: {}", l.getId());
            } catch (Exception e) {
                log.error("❌ Failed for log ID: {}", l.getId(), e);
            }
        }

        log.info("Billing job completed.");
    }
}