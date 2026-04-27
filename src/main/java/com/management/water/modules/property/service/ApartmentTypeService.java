package com.management.water.modules.property.service;

import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.repository.ApartmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentTypeService {

    private final ApartmentTypeRepository repository;

    public ApartmentType create(ApartmentType type) {
        return repository.save(type);
    }

    public List<ApartmentType> getAll() {
        return repository.findAll();
    }
}