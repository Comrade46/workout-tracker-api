package com.workouttracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_plan_exercises")
public class WorkoutPlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private Integer exerciseOrder;

    /*
     * Existing field.
     *
     * Kept for backward compatibility with the current
     * Workout Tracker application.
     */
    @Column(nullable = false)
    private Integer durationSeconds;

    /*
     * Existing field.
     */
    @Column(nullable = false)
    private Integer restSeconds;

    /*
     * New field.
     *
     * TIME:
     *     targetValue represents seconds.
     *
     * REPS:
     *     targetValue represents repetitions.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_type", length = 20)
    private ExerciseTrackingType trackingType;

    /*
     * New field.
     *
     * TIME example:
     *     30 = 30 seconds
     *
     * REPS example:
     *     12 = 12 repetitions
     */
    @Column(name = "target_value")
    private Integer targetValue;

    /*
     * New field.
     *
     * Example:
     *     Bench Press -> 3
     *     Plank -> 3
     */
    @Column(name = "target_sets")
    private Integer targetSets;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WorkoutPlanExercise() {
    }


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }


    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
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


    public ExerciseTrackingType getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(
            ExerciseTrackingType trackingType) {

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