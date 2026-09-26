package com.workouttracker.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * App admins: usernames listed in ADMIN_USERNAMES (comma separated).
 * Admins may edit built-in exercises, reset passwords and read feedback.
 */
@Component
public class AdminAccess {

    private final Set<String> adminUsernames;

    public AdminAccess(@Value("${workouttracker.app.admin-usernames:}") String adminUsernames) {
        this.adminUsernames = Arrays.stream(adminUsernames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isAdmin(String username) {
        return username != null && adminUsernames.contains(username);
    }

    public void requireAdmin(UserDetailsImpl user) {
        if (user == null || !isAdmin(user.getUsername())) {
            throw new AccessDeniedException("Only the app admin can do this.");
        }
    }
}
