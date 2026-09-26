package com.workouttracker.dto;

import java.time.LocalDateTime;

// Open "forgot password" request, as shown to the admin
public record PasswordResetRequestDTO(
        Long id,
        Long userId,
        String username,
        String email,
        String message,
        LocalDateTime createdAt
) {
}
