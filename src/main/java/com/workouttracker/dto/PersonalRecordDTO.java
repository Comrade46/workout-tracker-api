package com.workouttracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PersonalRecordDTO {

    private Long exerciseId;
    private String exerciseName;
    private String category;

    /*
     * REPS or TIME
     */
    private String trackingType;

    /*
     * REPS-based PR
     */
    private BigDecimal maxWeight;
    private Integer maxRepsAtMaxWeight;
    private BigDecimal estimatedOneRepMax;

    /*
     * TIME-based PR
     */
    private Integer bestDurationSeconds;

    private LocalDate achievedDate;

    public PersonalRecordDTO() {
    }

    public PersonalRecordDTO(
            Long exerciseId,
            String exerciseName,
            String category,
            String trackingType,
            BigDecimal maxWeight,
            Integer maxRepsAtMaxWeight,
            BigDecimal estimatedOneRepMax,
            Integer bestDurationSeconds,
            LocalDate achievedDate) {

        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.trackingType = trackingType;
        this.maxWeight = maxWeight;
        this.maxRepsAtMaxWeight = maxRepsAtMaxWeight;
        this.estimatedOneRepMax = estimatedOneRepMax;
        this.bestDurationSeconds = bestDurationSeconds;
        this.achievedDate = achievedDate;
    }

    // =====================================================
    // BUILDER
    // =====================================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long exerciseId;
        private String exerciseName;
        private String category;
        private String trackingType;

        private BigDecimal maxWeight;
        private Integer maxRepsAtMaxWeight;
        private BigDecimal estimatedOneRepMax;

        private Integer bestDurationSeconds;

        private LocalDate achievedDate;

        public Builder exerciseId(Long exerciseId) {
            this.exerciseId = exerciseId;
            return this;
        }

        public Builder exerciseName(String exerciseName) {
            this.exerciseName = exerciseName;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder trackingType(String trackingType) {
            this.trackingType = trackingType;
            return this;
        }

        public Builder maxWeight(BigDecimal maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public Builder maxRepsAtMaxWeight(
                Integer maxRepsAtMaxWeight) {

            this.maxRepsAtMaxWeight =
                    maxRepsAtMaxWeight;

            return this;
        }

        public Builder estimatedOneRepMax(
                BigDecimal estimatedOneRepMax) {

            this.estimatedOneRepMax =
                    estimatedOneRepMax;

            return this;
        }

        public Builder bestDurationSeconds(
                Integer bestDurationSeconds) {

            this.bestDurationSeconds =
                    bestDurationSeconds;

            return this;
        }

        public Builder achievedDate(
                LocalDate achievedDate) {

            this.achievedDate =
                    achievedDate;

            return this;
        }

        public PersonalRecordDTO build() {

            return new PersonalRecordDTO(
                    exerciseId,
                    exerciseName,
                    category,
                    trackingType,
                    maxWeight,
                    maxRepsAtMaxWeight,
                    estimatedOneRepMax,
                    bestDurationSeconds,
                    achievedDate
            );
        }
    }

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(BigDecimal maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Integer getMaxRepsAtMaxWeight() {
        return maxRepsAtMaxWeight;
    }

    public void setMaxRepsAtMaxWeight(
            Integer maxRepsAtMaxWeight) {

        this.maxRepsAtMaxWeight =
                maxRepsAtMaxWeight;
    }

    public BigDecimal getEstimatedOneRepMax() {
        return estimatedOneRepMax;
    }

    public void setEstimatedOneRepMax(
            BigDecimal estimatedOneRepMax) {

        this.estimatedOneRepMax =
                estimatedOneRepMax;
    }

    public Integer getBestDurationSeconds() {
        return bestDurationSeconds;
    }

    public void setBestDurationSeconds(
            Integer bestDurationSeconds) {

        this.bestDurationSeconds =
                bestDurationSeconds;
    }

    public LocalDate getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(
            LocalDate achievedDate) {

        this.achievedDate = achievedDate;
    }
}