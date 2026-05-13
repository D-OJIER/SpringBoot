package com.management.water.security.repository;

import com.management.water.security.entity.User;

import org.springframework.data.jpa.repository
        .JpaRepository;

import java.util.Optional;

public interface UserRepository
extends JpaRepository<User, Long> {

    Optional<User>
    findByUsername(String username);
}