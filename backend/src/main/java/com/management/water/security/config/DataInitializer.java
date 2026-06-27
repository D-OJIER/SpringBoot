package com.management.water.security.config;

import com.management.water.security.entity.Role;
import com.management.water.security.entity.User;
import com.management.water.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository repository;

  private final PasswordEncoder passwordEncoder;

  @Value("${admin.username}")
  private String adminUsername;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) {

    if (repository.findByUsername(adminUsername).isEmpty()) {

      User admin = new User();

      admin.setUsername(adminUsername);
      admin.setPassword(passwordEncoder.encode(adminPassword));

      admin.setRole(Role.ADMIN);

      repository.save(admin);
    }
  }
}
