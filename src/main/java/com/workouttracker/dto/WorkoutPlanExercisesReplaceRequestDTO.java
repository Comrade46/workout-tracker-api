package com.workouttracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/*
 * Request body for replacing all exercises of a workout plan
 * in one transaction.
 *
 * The list order becomes the exercise order.
 */
public class WorkoutPlanExercisesReplaceRequestDTO {

    @NotEmpty(message = "A workout plan must contain at least one exercise")
    @Valid
    private List<WorkoutPlanExerciseRequestDTO> exercises;

    public WorkoutPlanExercisesReplaceRequestDTO() {
    }

    public List<WorkoutPlanExerciseRequestDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutPlanExerciseRequestDTO> exercises) {
        this.exercises = exercises;
    }
}
