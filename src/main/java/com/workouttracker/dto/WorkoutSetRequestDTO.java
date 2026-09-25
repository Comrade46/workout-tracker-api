package com.workouttracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WorkoutSetRequestDTO {

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @NotNull(message = "Set number is required")
    @Min(value = 1, message = "Set number must be at least 1")
    private Integer setNumber;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Weight cannot be negative"
    )
    private BigDecimal weight;

    // 0 is allowed: TIME sets are sent with reps = 0
    @Min(
            value = 0,
            message = "Reps cannot be negative"
    )
    private Integer reps;

    // 0 is allowed: REPS sets are sent with durationSeconds = 0
    @Min(
            value = 0,
            message = "Duration cannot be negative"
    )
    private Integer durationSeconds;

    @Min(
            value = 1,
            message = "RPE must be between 1 and 10"
    )
    @Max(
            value = 10,
            message = "RPE must be between 1 and 10"
    )
    private Integer rpe;

    public WorkoutSetRequestDTO() {
    }

    public WorkoutSetRequestDTO(
            Long exerciseId,
            Integer setNumber,
            BigDecimal weight,
            Integer reps,
            Integer rpe) {

        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
    }

    public WorkoutSetRequestDTO(
            Long exerciseId,
            Integer setNumber,
            BigDecimal weight,
            Integer reps,
            Integer durationSeconds,
            Integer rpe) {

        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.durationSeconds = durationSeconds;
        this.rpe = rpe;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getRpe() {
        return rpe;
    }

    public void setRpe(Integer rpe) {
        this.rpe = rpe;
    }
}