package com.management.water.auth.config;

import com.management.water.auth.entity.Role;
import com.management.water.auth.entity.User;
import com.management.water.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the default admin user on first startup.
 * Admin credentials are read from environment variables (or .env file).
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${admin.username}")
  private String adminUsername;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) {
    if (userRepository.findByUsername(adminUsername).isEmpty()) {
      User admin = new User();
      admin.setUsername(adminUsername);
      admin.setPassword(passwordEncoder.encode(adminPassword));
      admin.setRole(Role.ADMIN);
      // Admin has no apartment
      userRepository.save(admin);
    }
  }
}
