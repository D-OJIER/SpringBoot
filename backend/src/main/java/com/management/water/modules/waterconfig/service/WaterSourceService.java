package com.management.water.modules.waterconfig.service;

import com.management.water.modules.waterconfig.entity.WaterSource;
import com.management.water.modules.waterconfig.repository.WaterSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterSourceService {

    private final WaterSourceRepository repository;

    public WaterSource create(WaterSource source) {
        return repository.save(source);
    }

    public List<WaterSource> getAll() {
        return repository.findAll();
    }
}