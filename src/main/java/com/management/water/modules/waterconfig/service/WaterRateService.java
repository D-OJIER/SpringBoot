package com.management.water.modules.waterconfig.service;

import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterRateService {

    private final WaterRateRepository rateRepository;
    private final WaterSourceRepository sourceRepository;

    public WaterRate create(WaterRate rate) {

        WaterSource source = sourceRepository.findById(rate.getSource().getId())
                .orElseThrow(() -> new RuntimeException("WaterSource not found"));

        rate.setSource(source);

        if (rate.getMinLitres() > rate.getMaxLitres()) {
            throw new RuntimeException("Invalid slab range");
        }

        return rateRepository.save(rate);
    }

    public List<WaterRate> getAll() {
        return rateRepository.findAll();
    }
}
