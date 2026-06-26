package com.management.water.property.service;

import com.management.water.property.entity.Apartment;
import com.management.water.property.entity.ApartmentType;
import com.management.water.property.entity.Block;
import com.management.water.property.exception.ApiException;
import com.management.water.property.repository.ApartmentRepository;
import com.management.water.property.repository.ApartmentTypeRepository;
import com.management.water.property.repository.BlockRepository;
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
    Block block =
        blockRepository
            .findById(apartment.getBlock().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Block not found"));

    ApartmentType type =
        typeRepository
            .findById(apartment.getType().getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ApartmentType not found"));

    apartment.setBlock(block);
    apartment.setType(type);

    return apartmentRepository.save(apartment);
  }

  public List<Apartment> getAll() {
    return apartmentRepository.findAll();
  }

  public Apartment getById(Long id) {
    return apartmentRepository
        .findById(id)
        .orElseThrow(() -> new ApiException.NotFoundException("Apartment not found"));
  }
}
