package com.workouttracker.dto;

// Returned once to the admin, who passes it on to the user
public record TemporaryPasswordDTO(
        Long userId,
        String username,
        String temporaryPassword
) {
}
