package com.management.water.property.service;

import com.management.water.property.entity.ApartmentType;
import com.management.water.property.repository.ApartmentTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
