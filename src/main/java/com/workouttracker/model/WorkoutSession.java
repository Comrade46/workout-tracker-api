package com.workouttracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "workout_date", nullable = false)
    private LocalDate workoutDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "session",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<WorkoutSet> sets = new ArrayList<>();

    public WorkoutSession() {
    }

    public WorkoutSession(
            Long id,
            Long userId,
            LocalDate workoutDate,
            String notes,
            Integer durationMinutes,
            LocalDateTime createdAt,
            List<WorkoutSet> sets) {

        this.id = id;
        this.userId = userId;
        this.workoutDate = workoutDate;
        this.notes = notes;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
        this.sets = sets != null ? sets : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private LocalDate workoutDate;
        private String notes;
        private Integer durationMinutes;
        private LocalDateTime createdAt;
        private List<WorkoutSet> sets = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder workoutDate(LocalDate workoutDate) {
            this.workoutDate = workoutDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder sets(List<WorkoutSet> sets) {
            this.sets = sets;
            return this;
        }

        public WorkoutSession build() {

            return new WorkoutSession(
                    id,
                    userId,
                    workoutDate,
                    notes,
                    durationMinutes,
                    createdAt,
                    sets
            );
        }
    }

    public void addSet(WorkoutSet workoutSet) {

        if (workoutSet == null) {
            return;
        }

        sets.add(workoutSet);
        workoutSet.setSession(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }
}