package com.workouttracker.dto;

public class WorkoutPlanExerciseResponse {

    private Long id;

    private Long workoutPlanId;
    private String workoutPlanName;

    private Long exerciseId;
    private String exerciseName;
    private String category;
    private String workoutType;
    private String equipment;

    private Integer exerciseOrder;

    /*
     * Existing fields.
     */
    private Integer durationSeconds;
    private Integer restSeconds;

    /*
     * New tracking fields.
     */
    private String trackingType;
    private Integer targetValue;
    private Integer targetSets;


    public WorkoutPlanExerciseResponse() {
    }


    public WorkoutPlanExerciseResponse(
            Long id,
            Long workoutPlanId,
            String workoutPlanName,
            Long exerciseId,
            String exerciseName,
            String category,
            String workoutType,
            String equipment,
            Integer exerciseOrder,
            Integer durationSeconds,
            Integer restSeconds,
            String trackingType,
            Integer targetValue,
            Integer targetSets) {

        this.id = id;
        this.workoutPlanId = workoutPlanId;
        this.workoutPlanName = workoutPlanName;

        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.category = category;
        this.workoutType = workoutType;
        this.equipment = equipment;

        this.exerciseOrder = exerciseOrder;

        this.durationSeconds = durationSeconds;
        this.restSeconds = restSeconds;

        this.trackingType = trackingType;
        this.targetValue = targetValue;
        this.targetSets = targetSets;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getWorkoutPlanId() {
        return workoutPlanId;
    }

    public void setWorkoutPlanId(Long workoutPlanId) {
        this.workoutPlanId = workoutPlanId;
    }


    public String getWorkoutPlanName() {
        return workoutPlanName;
    }

    public void setWorkoutPlanName(String workoutPlanName) {
        this.workoutPlanName = workoutPlanName;
    }


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


    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }


    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }


    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
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


    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }


    public Integer getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Integer targetValue) {
        this.targetValue = targetValue;
    }


    public Integer getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(Integer targetSets) {
        this.targetSets = targetSets;
    }
}