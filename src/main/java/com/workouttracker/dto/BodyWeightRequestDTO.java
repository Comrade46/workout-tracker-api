package com.workouttracker.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BodyWeightRequestDTO(
        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Weight is required")
        @DecimalMin(value = "25.0", message = "Weight must be between 25 and 300 kg")
        @DecimalMax(value = "300.0", message = "Weight must be between 25 and 300 kg")
        BigDecimal weightKg
) {
}
