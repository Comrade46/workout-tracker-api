package com.workouttracker.dto;

import com.workouttracker.model.WorkoutType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExerciseRequestDTO {

    @NotBlank(message = "Exercise name is required")
    @Size(min = 2, max = 100, message = "Exercise name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String category;

    @NotNull(message = "Workout type is required (GYM, HOME, or BOTH)")
    private WorkoutType workoutType;

    @NotBlank(message = "Equipment is required")
    @Size(max = 50, message = "Equipment name must not exceed 50 characters")
    private String equipment;

    @Min(value = 1, message = "Exercise duration must be at least 1 second")
    private Integer durationSeconds = 30;

    @Min(value = 0, message = "Rest time cannot be negative")
    private Integer restSeconds = 15;

    public ExerciseRequestDTO() {}

    public ExerciseRequestDTO(String name, String category, WorkoutType workoutType, String equipment,
                              Integer durationSeconds, Integer restSeconds) {
        this.name = name;
        this.category = category;
        this.workoutType = workoutType;
        this.equipment = equipment;
        this.durationSeconds = durationSeconds;
        this.restSeconds = restSeconds;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public WorkoutType getWorkoutType() { return workoutType; }
    public void setWorkoutType(WorkoutType workoutType) { this.workoutType = workoutType; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public Integer getDurationSeconds() { return durationSeconds == null ? 30 : durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public Integer getRestSeconds() { return restSeconds == null ? 15 : restSeconds; }
    public void setRestSeconds(Integer restSeconds) { this.restSeconds = restSeconds; }
}
