package com.management.water.security.utils;

import com.management.water.security.entity.User;
import com.management.water.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean isCurrentUserResident() {
        User user = getCurrentUser();
        return user != null && "RESIDENT".equals(user.getRole().name());
    }

    public boolean isCurrentUserAdmin() {
        User user = getCurrentUser();
        return user != null && "ADMIN".equals(user.getRole().name());
    }
}
