package com.autoecole.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum UserRole {
    ADMIN("Administrator with full access"),
    MANAGER("Manager with limited admin access"),
    STAFF("Staff member with basic access");

    private final String description;
}