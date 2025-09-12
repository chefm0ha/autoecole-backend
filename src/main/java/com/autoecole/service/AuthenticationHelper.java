package com.autoecole.service;

import com.autoecole.config.CustomUserDetails;
import com.autoecole.enums.UserRole;
import com.autoecole.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationHelper {

    /**
     * Get the currently authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        }

        return null;
    }

    /**
     * Get the role of the currently authenticated user
     */
    public UserRole getCurrentUserRole() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getRole() : null;
    }

    /**
     * Check if the current user is an admin
     */
    public boolean isCurrentUserAdmin() {
        UserRole currentRole = getCurrentUserRole();
        return currentRole == UserRole.ADMIN;
    }

    /**
     * Check if the current user is staff
     */
    public boolean isCurrentUserStaff() {
        UserRole currentRole = getCurrentUserRole();
        return currentRole == UserRole.STAFF;
    }
}