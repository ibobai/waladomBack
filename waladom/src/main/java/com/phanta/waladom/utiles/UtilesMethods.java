package com.phanta.waladom.utiles;

import java.util.Arrays;
import java.util.List;

public class UtilesMethods {
    // Predefined roles
    private static final List<String> ROLE_IDS = Arrays.asList(
            "ROLE_ADMIN",
            "ROLE_CONTENT_MANAGER",
            "ROLE_MODERATOR",
            "ROLE_MEMBERSHIP_REVIEWER",
            "ROLE_USER"
    );

    /**
     * Checks if the given roleId exists in the predefined roles (case-insensitive).
     *
     * @param roleId The role ID to check.
     * @return True if the roleId exists, otherwise false.
     */
    public static boolean isRoleIdValid(String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return false;
        }
        // Check for case-insensitive match
        return ROLE_IDS.stream()
                .anyMatch(role -> role.equalsIgnoreCase(roleId));
    }
}
