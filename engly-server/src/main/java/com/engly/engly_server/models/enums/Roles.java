package com.engly.engly_server.models.enums;

import lombok.Getter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.engly.engly_server.models.enums.Authority.*;

@Getter
public enum Roles {
    ROLE_ADMIN(EnumSet.of(READ, WRITE, DELETE, CREATE_GLOBAL, UPDATE_GLOBAL, DELETE_GLOBAL, ADMIN, AUTHORIZE, PASSWORD_RESET)),
    ROLE_MANAGER(EnumSet.of(READ, WRITE, DELETE, AUTHORIZE, PASSWORD_RESET)),
    ROLE_USER(EnumSet.of(READ, WRITE, AUTHORIZE, PASSWORD_RESET)),
    ROLE_GOOGLE(EnumSet.of(ADDITIONAL_INFO, AUTHORIZE, PASSWORD_RESET, READ)),
    ROLE_SYSADMIN(ROLE_ADMIN.permissions), // inherits from ROLE_ADMIN
    ROLE_NOT_VERIFIED(EnumSet.of(NOT_VERIFIED, AUTHORIZE, PASSWORD_RESET)),
    ROLE_BAN(EnumSet.of(AUTHORIZE, PASSWORD_RESET, LIMITED_ACCESS));

    private final Set<Authority> permissions;

    Roles(Set<Authority> permissions) {
        this.permissions = permissions;
    }

    public static Set<Authority> getPermissionsForRoles(Collection<String> roles) {
        return roles.stream()
                .map(Roles::valueOf)
                .flatMap(r -> r.getPermissions().stream())
                .collect(Collectors.toSet());
    }
}
