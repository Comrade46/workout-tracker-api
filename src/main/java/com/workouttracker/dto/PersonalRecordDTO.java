package com.workouttracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PersonalRecordDTO {

    private Long exerciseId;
    private String exerciseName;
    private String category;
    private BigDecimal maxWeight;
    private Integer maxRepsAtMaxWeight;
    private BigDecimal estimatedOneRepMax; // Calculated via Brzycki formula
    private LocalDate achievedDate;

    public PersonalRecordDTO() {}

    public PersonalRecordDTO(Long exerciseId, String exerciseName, String category, BigDecimal maxWeight, Integer maxRepsAtMaxWeight, BigDecimal estimatedOneRepMax, LocalDate achievedDate) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.maxWeight = maxWeight;
        this.maxRepsAtMaxWeight = maxRepsAtMaxWeight;
        this.estimatedOneRepMax = estimatedOneRepMax;
        this.achievedDate = achievedDate;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long exerciseId;
        private String exerciseName;
        private String category;
        private BigDecimal maxWeight;
        private Integer maxRepsAtMaxWeight;
        private BigDecimal estimatedOneRepMax;
        private LocalDate achievedDate;

        public Builder exerciseId(Long exerciseId) { this.exerciseId = exerciseId; return this; }
        public Builder exerciseName(String exerciseName) { this.exerciseName = exerciseName; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder maxWeight(BigDecimal maxWeight) { this.maxWeight = maxWeight; return this; }
        public Builder maxRepsAtMaxWeight(Integer maxRepsAtMaxWeight) { this.maxRepsAtMaxWeight = maxRepsAtMaxWeight; return this; }
        public Builder estimatedOneRepMax(BigDecimal estimatedOneRepMax) { this.estimatedOneRepMax = estimatedOneRepMax; return this; }
        public Builder achievedDate(LocalDate achievedDate) { this.achievedDate = achievedDate; return this; }
        public PersonalRecordDTO build() {
            return new PersonalRecordDTO(exerciseId, exerciseName, category, maxWeight, maxRepsAtMaxWeight, estimatedOneRepMax, achievedDate);
        }
    }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getMaxWeight() { return maxWeight; }
    public void setMaxWeight(BigDecimal maxWeight) { this.maxWeight = maxWeight; }

    public Integer getMaxRepsAtMaxWeight() { return maxRepsAtMaxWeight; }
    public void setMaxRepsAtMaxWeight(Integer maxRepsAtMaxWeight) { this.maxRepsAtMaxWeight = maxRepsAtMaxWeight; }

    public BigDecimal getEstimatedOneRepMax() { return estimatedOneRepMax; }
    public void setEstimatedOneRepMax(BigDecimal estimatedOneRepMax) { this.estimatedOneRepMax = estimatedOneRepMax; }

    public LocalDate getAchievedDate() { return achievedDate; }
    public void setAchievedDate(LocalDate achievedDate) { this.achievedDate = achievedDate; }
}
