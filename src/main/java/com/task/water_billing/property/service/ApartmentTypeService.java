package com.task.water_billing.property.service;

import com.task.water_billing.property.entity.ApartmentType;
import com.task.water_billing.property.repository.ApartmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApartmentTypeService {

    private final ApartmentTypeRepository repo;

    public ApartmentType create(ApartmentType type) {
        return repo.save(type);
    }
}