package com.workouttracker.dto;

import com.workouttracker.model.ExerciseTrackingType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WorkoutPlanExerciseRequestDTO {

    private Long workoutPlanId;

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @Min(value = 1, message = "Exercise order must be at least 1")
    private Integer exerciseOrder;

    @Min(value = 0, message = "Rest time cannot be negative")
    private Integer restSeconds;

    private ExerciseTrackingType trackingType;

    @Min(value = 1, message = "Target value must be at least 1")
    private Integer targetValue;

    @Min(value = 1, message = "Target sets must be at least 1")
    private Integer targetSets;

    public WorkoutPlanExerciseRequestDTO() {
    }

    public Long getWorkoutPlanId() {
        return workoutPlanId;
    }

    public void setWorkoutPlanId(Long workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public ExerciseTrackingType getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(ExerciseTrackingType trackingType) {
        this.trackingType = trackingType;
    }

    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }

    public Integer getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(Integer targetSets) {
        this.targetSets = targetSets;
    }
}