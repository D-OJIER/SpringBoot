package com.management.water.modules.waterconfig.service;

import com.management.water.modules.waterconfig.entity.WaterRate;
import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.WaterRateRepository;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.management.water.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterRateService {

    private final WaterRateRepository rateRepository;
    private final WaterSourceRepository sourceRepository;

    public WaterRate create(WaterRate rate) {

        WaterSource source = sourceRepository.findById(rate.getSource().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

        rate.setSource(source);

        if (rate.getMinLitres() > rate.getMaxLitres()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
        }

        validateSlabs(source.getId());

        return rateRepository.save(rate);
    }

    private void validateSlabs(Long sourceId) {

        List<WaterRate> rates =
                rateRepository.findBySourceIdOrderByMinLitresAsc(sourceId);

        if (rates.isEmpty()) return;

        if (rates.get(0).getMinLitres() != 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs must start at 0");
        }

        for (int i = 0; i < rates.size() - 1; i++) {

            WaterRate current = rates.get(i);
            WaterRate next = rates.get(i + 1);

            if (current.getMaxLitres() + 1 != next.getMinLitres()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs are not continuous");
            }

            if (current.getMaxLitres() >= next.getMinLitres()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slabs overlap");
            }
        }
    }

    public void deleteById(Long id) {

        WaterRate rate =
                rateRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));
        if (
            rate.getEffectiveFrom()
                .isBefore(java.time.LocalDate.now())
        ) {

            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cannot delete historical rates"
            );
        }

        rateRepository.deleteById(id);
    }
    
    public List<WaterRate> getAll() {
        return rateRepository.findAll();
    }

    public WaterRate update(Long id, WaterRate updated) {

        WaterRate existing = rateRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rate not found"));

        WaterSource source = sourceRepository.findById(updated.getSource().getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WaterSource not found"));

        if (updated.getMinLitres() > updated.getMaxLitres()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid slab range");
        }

        List<WaterRate> rates =
                rateRepository.findBySourceIdOrderByMinLitresAsc(source.getId());

        for (WaterRate rate : rates) {

            if (rate.getId().equals(id)) {
                continue;
            }

            boolean overlap =
                    updated.getMinLitres() <= rate.getMaxLitres()
                    && updated.getMaxLitres() >= rate.getMinLitres();

            if (overlap) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Slab overlaps existing slabs");
            }
        }

        existing.setMinLitres(updated.getMinLitres());
        existing.setMaxLitres(updated.getMaxLitres());
        existing.setRatePerLitre(updated.getRatePerLitre());
        existing.setSource(source);

        return rateRepository.save(existing);
    }
}
