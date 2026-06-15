package com.management.water.modules.property.service;

import com.management.water.modules.property.entity.ApartmentType;
import com.management.water.modules.property.repository.ApartmentTypeRepository;
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
