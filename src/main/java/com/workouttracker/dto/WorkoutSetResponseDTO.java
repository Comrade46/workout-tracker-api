package com.workouttracker.dto;

import java.math.BigDecimal;

public class WorkoutSetResponseDTO {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String category;
    private Integer setNumber;
    private BigDecimal weight;
    private Integer reps;
    private Integer rpe;
    private BigDecimal volume; // weight * reps

    public WorkoutSetResponseDTO() {}

    public WorkoutSetResponseDTO(Long id, Long exerciseId, String exerciseName, String category, Integer setNumber, BigDecimal weight, Integer reps, Integer rpe, BigDecimal volume) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
        this.volume = volume;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private String category;
        private Integer setNumber;
        private BigDecimal weight;
        private Integer reps;
        private Integer rpe;
        private BigDecimal volume;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder exerciseId(Long exerciseId) { this.exerciseId = exerciseId; return this; }
        public Builder exerciseName(String exerciseName) { this.exerciseName = exerciseName; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder setNumber(Integer setNumber) { this.setNumber = setNumber; return this; }
        public Builder weight(BigDecimal weight) { this.weight = weight; return this; }
        public Builder reps(Integer reps) { this.reps = reps; return this; }
        public Builder rpe(Integer rpe) { this.rpe = rpe; return this; }
        public Builder volume(BigDecimal volume) { this.volume = volume; return this; }
        public WorkoutSetResponseDTO build() {
            return new WorkoutSetResponseDTO(id, exerciseId, exerciseName, category, setNumber, weight, reps, rpe, volume);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getRpe() { return rpe; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }

    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
}
