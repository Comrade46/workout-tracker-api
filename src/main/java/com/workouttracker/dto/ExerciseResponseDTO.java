package com.workouttracker.dto;

import com.workouttracker.model.WorkoutType;
import java.time.LocalDateTime;

public class ExerciseResponseDTO {

    private Long id;
    private String name;
    private String category;
    private WorkoutType workoutType;
    private String equipment;
    private Boolean isCustom;
    private Integer durationSeconds;
    private Integer restSeconds;
    private LocalDateTime createdAt;

    public ExerciseResponseDTO() {}

    public ExerciseResponseDTO(Long id, String name, String category, WorkoutType workoutType, String equipment,
                               Boolean isCustom, Integer durationSeconds, Integer restSeconds, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.workoutType = workoutType;
        this.equipment = equipment;
        this.isCustom = isCustom;
        this.durationSeconds = durationSeconds;
        this.restSeconds = restSeconds;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String category;
        private WorkoutType workoutType;
        private String equipment;
        private Boolean isCustom;
        private Integer durationSeconds = 30;
        private Integer restSeconds = 15;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder workoutType(WorkoutType workoutType) { this.workoutType = workoutType; return this; }
        public Builder equipment(String equipment) { this.equipment = equipment; return this; }
        public Builder isCustom(Boolean isCustom) { this.isCustom = isCustom; return this; }
        public Builder durationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; return this; }
        public Builder restSeconds(Integer restSeconds) { this.restSeconds = restSeconds; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ExerciseResponseDTO build() {
            return new ExerciseResponseDTO(id, name, category, workoutType, equipment, isCustom, durationSeconds, restSeconds, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public WorkoutType getWorkoutType() { return workoutType; }
    public void setWorkoutType(WorkoutType workoutType) { this.workoutType = workoutType; }
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public Boolean getIsCustom() { return isCustom; }
    public void setIsCustom(Boolean isCustom) { this.isCustom = isCustom; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
    public Integer getRestSeconds() { return restSeconds; }
    public void setRestSeconds(Integer restSeconds) { this.restSeconds = restSeconds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
