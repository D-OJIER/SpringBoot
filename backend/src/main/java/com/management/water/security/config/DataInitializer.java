package com.management.water.security.config;

import com.management.water.security.entity.Role;
import com.management.water.security.entity.User;
import com.management.water.security.repository
        .UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto
        .password.PasswordEncoder;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
implements CommandLineRunner {

    private final UserRepository
            repository;

    private final PasswordEncoder
            passwordEncoder;

    @Override
    public void run(String... args) {

        if (
            repository.findByUsername(
                    "admin"
            ).isEmpty()
        ) {

            User admin = new User();

            admin.setUsername("admin");

            admin.setPassword(
                    passwordEncoder.encode(
                            "admin123"
                    )
            );

            admin.setRole(Role.ADMIN);

            repository.save(admin);
        }
    }
}