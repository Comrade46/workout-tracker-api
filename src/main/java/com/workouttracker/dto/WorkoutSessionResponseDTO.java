package com.workouttracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WorkoutSessionResponseDTO {

    private Long id;
    private Long userId;
    private LocalDate workoutDate;
    private String notes;
    private Integer durationMinutes;
    private BigDecimal totalVolume; // Sum of all sets' volume
    private Integer totalSets;
    private LocalDateTime createdAt;
    private List<WorkoutSetResponseDTO> sets;

    public WorkoutSessionResponseDTO() {}

    public WorkoutSessionResponseDTO(Long id, Long userId, LocalDate workoutDate, String notes, Integer durationMinutes, BigDecimal totalVolume, Integer totalSets, LocalDateTime createdAt, List<WorkoutSetResponseDTO> sets) {
        this.id = id;
        this.userId = userId;
        this.workoutDate = workoutDate;
        this.notes = notes;
        this.durationMinutes = durationMinutes;
        this.totalVolume = totalVolume;
        this.totalSets = totalSets;
        this.createdAt = createdAt;
        this.sets = sets;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long userId;
        private LocalDate workoutDate;
        private String notes;
        private Integer durationMinutes;
        private BigDecimal totalVolume;
        private Integer totalSets;
        private LocalDateTime createdAt;
        private List<WorkoutSetResponseDTO> sets;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder workoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public Builder totalVolume(BigDecimal totalVolume) { this.totalVolume = totalVolume; return this; }
        public Builder totalSets(Integer totalSets) { this.totalSets = totalSets; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder sets(List<WorkoutSetResponseDTO> sets) { this.sets = sets; return this; }
        public WorkoutSessionResponseDTO build() {
            return new WorkoutSessionResponseDTO(id, userId, workoutDate, notes, durationMinutes, totalVolume, totalSets, createdAt, sets);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getTotalVolume() { return totalVolume; }
    public void setTotalVolume(BigDecimal totalVolume) { this.totalVolume = totalVolume; }

    public Integer getTotalSets() { return totalSets; }
    public void setTotalSets(Integer totalSets) { this.totalSets = totalSets; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<WorkoutSetResponseDTO> getSets() { return sets; }
    public void setSets(List<WorkoutSetResponseDTO> sets) { this.sets = sets; }
}
