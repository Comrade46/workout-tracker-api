package com.workouttracker.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

// Height, goals and target weight (all optional).
public record BodyProfileDTO(
        @Min(value = 100, message = "Height must be between 100 and 250 cm")
        @Max(value = 250, message = "Height must be between 100 and 250 cm")
        Integer heightCm,

        @Min(value = 1, message = "Weekly goal must be between 1 and 7 workouts")
        @Max(value = 7, message = "Weekly goal must be between 1 and 7 workouts")
        Integer weeklyGoal,

        @Pattern(regexp = "^(BUILD_MUSCLE|LOSE_WEIGHT|GET_FIT|STAY_ACTIVE)$",
                message = "Unknown goal")
        String fitnessGoal,

        @DecimalMin(value = "25.0", message = "Target weight must be between 25 and 300 kg")
        @DecimalMax(value = "300.0", message = "Target weight must be between 25 and 300 kg")
        BigDecimal targetWeightKg
) {
}
