package com.workouttracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BodyWeightDTO(Long id, LocalDate date, BigDecimal weightKg) {
}
