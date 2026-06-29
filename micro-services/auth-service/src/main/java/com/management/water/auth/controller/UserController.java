package com.management.water.auth.controller;

import com.management.water.auth.dto.UserCreateRequest;
import com.management.water.auth.dto.UserResponse;
import com.management.water.auth.entity.Role;
import com.management.water.auth.entity.User;
import com.management.water.auth.exception.ApiException;
import com.management.water.auth.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only user management controller.
 *
 * <p>Change from monolith: No longer injects ApartmentRepository.
 * The apartmentId from the request is stored as a plain FK on the User entity.
 * When property-service is available, we'll add a Feign call to validate the apartment exists.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping
  public UserResponse createResident(@Valid @RequestBody UserCreateRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ApiException.ConflictException("Username already exists");
    }

    // TODO (Phase 2): Add Feign call to property-service to verify apartmentId exists
    // ApartmentDto apartment = propertyServiceClient.getApartment(request.getApartmentId());

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.RESIDENT);
    user.setApartmentId(request.getApartmentId());

    User saved = userRepository.save(user);
    return toResponse(saved);
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @GetMapping("/username/{username}")
  @PreAuthorize("isAuthenticated()")
  public UserResponse getUserByUsername(@PathVariable String username) {
    return userRepository
        .findByUsername(username)
        .map(this::toResponse)
        .orElseThrow(() -> new ApiException.NotFoundException("User not found"));
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable Long id) {
    return userRepository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(() -> new ApiException.NotFoundException("User not found"));
  }

  private UserResponse toResponse(User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setRole(user.getRole().name());
    response.setApartmentId(user.getApartmentId());
    return response;
  }
}
