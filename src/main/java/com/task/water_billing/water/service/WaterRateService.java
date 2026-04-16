package com.task.water_billing.water.service;

import com.task.water_billing.water.entity.WaterRate;
import com.task.water_billing.water.entity.WaterSource;
import com.task.water_billing.water.repository.WaterRateRepository;
import com.task.water_billing.water.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaterRateService {

    private final WaterRateRepository repo;
    private final WaterSourceRepository sourceRepo;

    public WaterRate create(Long sourceId, WaterRate rate) {

        WaterSource source = sourceRepo.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Source not found"));

        rate.setSource(source);
        return repo.save(rate);
    }
}