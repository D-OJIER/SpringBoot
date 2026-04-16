package com.task.water_billing.water.service;

import com.task.water_billing.water.entity.WaterSource;
import com.task.water_billing.water.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaterSourceService {

    private final WaterSourceRepository repo;

    public WaterSource create(WaterSource source) {
        return repo.save(source);
    }
}