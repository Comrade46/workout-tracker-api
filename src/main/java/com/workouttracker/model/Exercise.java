package com.workouttracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", nullable = false, length = 30)
    private WorkoutType workoutType;

    @Column(length = 100)
    private String equipment;

    @Column(name = "is_custom", nullable = false)
    private Boolean isCustom = false;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Exercise() {
    }

    public Exercise(
            Long id,
            String name,
            String category,
            WorkoutType workoutType,
            String equipment,
            Boolean isCustom,
            Integer durationSeconds,
            Integer restSeconds,
            LocalDateTime createdAt) {

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

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (isCustom == null) {
            isCustom = false;
        }

        if (durationSeconds == null) {
            durationSeconds = 30;
        }

        if (restSeconds == null) {
            restSeconds = 15;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String name;
        private String category;
        private WorkoutType workoutType;
        private String equipment;
        private Boolean isCustom;
        private Integer durationSeconds;
        private Integer restSeconds;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder workoutType(WorkoutType workoutType) {
            this.workoutType = workoutType;
            return this;
        }

        public Builder equipment(String equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder isCustom(Boolean isCustom) {
            this.isCustom = isCustom;
            return this;
        }

        public Builder durationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public Builder restSeconds(Integer restSeconds) {
            this.restSeconds = restSeconds;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Exercise build() {
            return new Exercise(
                    id,
                    name,
                    category,
                    workoutType,
                    equipment,
                    isCustom,
                    durationSeconds,
                    restSeconds,
                    createdAt
            );
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}