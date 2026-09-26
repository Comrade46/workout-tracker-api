package com.workouttracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class WorkoutSessionRequestDTO {

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    @Min(value = 0, message = "Duration cannot be negative")
    @Max(value = 1440, message = "Duration cannot be more than 24 hours")
    private Integer durationMinutes;

    // Optional ID from the phone's upload queue (prevents duplicates)
    @Size(max = 64, message = "Client ID must not exceed 64 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]*$", message = "Client ID may only contain letters, digits and dashes")
    private String clientId;

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

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public List<WorkoutSetRequestDTO> getSets() { return sets; }
    public void setSets(List<WorkoutSetRequestDTO> sets) { this.sets = sets; }
}