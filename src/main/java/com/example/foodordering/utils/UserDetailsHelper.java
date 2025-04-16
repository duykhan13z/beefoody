package com.example.foodordering.utils;

import com.example.foodordering.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class UserDetailsHelper {
    public static Optional<CustomUserDetails> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
          return Optional.of(userDetails);
        }
        return Optional.empty();
    }
}
