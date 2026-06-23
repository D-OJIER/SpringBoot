package com.management.water.security.controller;

import com.management.water.modules.common.exception.ApiException;
import com.management.water.modules.property.entity.Apartment;
import com.management.water.modules.property.repository.ApartmentRepository;
import com.management.water.security.dto.UserCreateRequest;
import com.management.water.security.dto.UserResponse;
import com.management.water.security.entity.Role;
import com.management.water.security.entity.User;
import com.management.water.security.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

  private final UserRepository userRepository;
  private final ApartmentRepository apartmentRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping
  public UserResponse createResident(@Valid @RequestBody UserCreateRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new ApiException.ConflictException("Username already exists");
    }

    Apartment apartment = apartmentRepository.findById(request.getApartmentId())
        .orElseThrow(() -> new ApiException.NotFoundException("Apartment not found"));

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.RESIDENT);
    user.setApartment(apartment);

    User saved = userRepository.save(user);
    return toResponse(saved);
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  private UserResponse toResponse(User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setRole(user.getRole().name());
    if (user.getApartment() != null) {
      response.setApartmentId(user.getApartment().getId());
      response.setApartmentNumber(user.getApartment().getNumber());
    }
    return response;
  }
}
