package com.workouttracker.dto;

import java.time.LocalDateTime;

public record FeedbackDTO(
        Long id,
        String username,
        String message,
        String appVersion,
        String page,
        String device,
        LocalDateTime createdAt,
        boolean resolved
) {
}
