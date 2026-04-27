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

        validateSlabs(source.getId());

        return rateRepository.save(rate);
    }

    private void validateSlabs(Long sourceId) {

        List<WaterRate> rates =
                rateRepository.findBySourceIdOrderByMinLitresAsc(sourceId);

        if (rates.isEmpty()) return;

        // ⚔️ Rule 1: Must start at 0
        if (rates.get(0).getMinLitres() != 0) {
            throw new RuntimeException("Slabs must start at 0");
        }

        for (int i = 0; i < rates.size() - 1; i++) {

            WaterRate current = rates.get(i);
            WaterRate next = rates.get(i + 1);

            // ⚔️ Rule 2: Continuity (no gaps)
            if (current.getMaxLitres() + 1 != next.getMinLitres()) {
                throw new RuntimeException("Slabs are not continuous");
            }

            // ⚔️ Rule 3: No overlap
            if (current.getMaxLitres() >= next.getMinLitres()) {
                throw new RuntimeException("Slabs overlap");
            }
        }
    }

    public List<WaterRate> getAll() {
        return rateRepository.findAll();
    }
}
