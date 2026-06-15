package com.management.water.modules.property.service;

import com.management.water.modules.common.exception.ApiException;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.entity.Block;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.modules.property.repository.ApartmentTypeRepository;
import com.management.water.modules.property.repository.BlockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApartmentService {

  private final ApartmentRepository apartmentRepository;
  private final BlockRepository blockRepository;
  private final ApartmentTypeRepository typeRepository;

  public Apartment create(Apartment apartment) {

    // 🔹 Fetch real Block from DB
    Block block =
        blockRepository
            .findById(apartment.getBlock().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Block not found"));

    // 🔹 Fetch real ApartmentType from DB
    ApartmentType type =
        typeRepository
            .findById(apartment.getType().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ApartmentType not found"));

    // 🔹 Attach full objects
    apartment.setBlock(block);
    apartment.setType(type);

    // 🔹 Save
    return apartmentRepository.save(apartment);
  }

  public List<Apartment> getAll() {
    return apartmentRepository.findAll();
  }
}
