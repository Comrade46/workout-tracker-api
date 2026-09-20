package com.workouttracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class WorkoutSessionRequestDTO {

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    private String notes;

    private Integer durationMinutes;

    @NotEmpty(message = "A workout session must contain at least one set")
    @Valid
    private List<WorkoutSetRequestDTO> sets;

    public WorkoutSessionRequestDTO() {}

    public WorkoutSessionRequestDTO(LocalDate workoutDate, String notes, Integer durationMinutes, List<WorkoutSetRequestDTO> sets) {
        this.workoutDate = workoutDate;
        this.notes = notes;
        this.durationMinutes = durationMinutes;
        this.sets = sets;
    }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public List<WorkoutSetRequestDTO> getSets() { return sets; }
    public void setSets(List<WorkoutSetRequestDTO> sets) { this.sets = sets; }
}