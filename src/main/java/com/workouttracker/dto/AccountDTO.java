package com.workouttracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

// The logged-in user's account (profile page) and the admin's user list.
public record AccountDTO(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt,
        boolean admin,
        boolean mustChangePassword,
        Long workoutCount,
        LocalDate lastWorkoutDate
) {
}
