package com.task.water_billing.property.service;

import com.task.water_billing.property.entity.*;
import com.task.water_billing.property.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository repo;
    private final BlockRepository blockRepo;
    private final ApartmentTypeRepository typeRepo;

    public Apartment create(Long blockId, Long typeId, String number) {

        Block block = blockRepo.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found"));

        ApartmentType type = typeRepo.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Type not found"));

        Apartment apt = new Apartment();
        apt.setApartmentNumber(number);
        apt.setBlock(block);
        apt.setType(type);
        apt.setStatus("ACTIVE");

        return repo.save(apt);
    }
}
