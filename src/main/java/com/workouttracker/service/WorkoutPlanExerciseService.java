package com.workouttracker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workouttracker.dto.WorkoutPlanExerciseRequestDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.ExerciseTrackingType;
import com.workouttracker.model.WorkoutPlan;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutPlanExerciseRepository;
import com.workouttracker.repository.WorkoutPlanRepository;

@Service
public class WorkoutPlanExerciseService {

    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutPlanExerciseService(
            WorkoutPlanExerciseRepository workoutPlanExerciseRepository,
            WorkoutPlanRepository workoutPlanRepository,
            ExerciseRepository exerciseRepository) {

        this.workoutPlanExerciseRepository = workoutPlanExerciseRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseRepository = exerciseRepository;
    }

    /*
     * ============================================================
     * EXISTING / BACKWARD-COMPATIBLE METHOD
     * ============================================================
     *
     * Used by the existing controller.
     *
     * Old exercises are treated as TIME based exercises so that
     * existing workout plans continue to work.
     */
    public WorkoutPlanExercise addExerciseToWorkoutPlan(
            Long workoutPlanId,
            Long exerciseId,
            Integer durationSeconds,
            Integer restSeconds,
            Integer exerciseOrder,
            String username) {

        WorkoutPlan workoutPlan =
                findOwnedPlan(workoutPlanId, username);

        Exercise exercise =
                findVisibleExercise(exerciseId, workoutPlan);

        WorkoutPlanExercise workoutPlanExercise =
                new WorkoutPlanExercise();

        workoutPlanExercise.setWorkoutPlan(workoutPlan);
        workoutPlanExercise.setExercise(exercise);
        workoutPlanExercise.setExerciseOrder(exerciseOrder);

        workoutPlanExercise.setRestSeconds(
                restSeconds != null ? restSeconds : 15);

        /*
         * Tracking type always follows the exercise.
         * durationSeconds is the planned target only for TIME exercises.
         */
        ExerciseTrackingType trackingType =
                ExerciseTracking.resolve(exercise);

        int target =
                ExerciseTracking.planTarget(
                        exercise,
                        ExerciseTrackingType.TIME,
                        durationSeconds);

        workoutPlanExercise.setTrackingType(trackingType);
        workoutPlanExercise.setTargetValue(target);
        workoutPlanExercise.setDurationSeconds(
                trackingType == ExerciseTrackingType.TIME ? target : 0);

        workoutPlanExercise.setTargetSets(1);

        return workoutPlanExerciseRepository.save(
                workoutPlanExercise);
    }

    /*
     * ============================================================
     * NEW CONFIGURED EXERCISE METHOD
     * ============================================================
     *
     * Supports:
     *
     * TIME
     * REPS
     *
     * Example:
     *
     * Bench Press -> REPS -> 12 -> 3 sets
     *
     * Plank -> TIME -> 30 seconds -> 3 sets
     */
    public WorkoutPlanExercise addConfiguredExercise(
            WorkoutPlanExerciseRequestDTO request,
            String username) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Workout plan exercise request cannot be null");
        }

        if (request.getWorkoutPlanId() == null) {
            throw new IllegalArgumentException(
                    "Workout plan ID is required");
        }

        WorkoutPlan workoutPlan =
                findOwnedPlan(request.getWorkoutPlanId(), username);

        Exercise exercise =
                findVisibleExercise(request.getExerciseId(), workoutPlan);

        return workoutPlanExerciseRepository.save(
                buildConfiguredExercise(
                        workoutPlan,
                        exercise,
                        request,
                        request.getExerciseOrder()));
    }

    /*
     * ============================================================
     * REPLACE ALL EXERCISES OF A PLAN (ATOMIC)
     * ============================================================
     *
     * Used when saving a plan from the UI. Old exercises are
     * removed and the new list is inserted in ONE transaction,
     * so a failure part-way leaves the plan unchanged.
     *
     * exerciseOrder is taken from the list position.
     */
    @Transactional
    public List<WorkoutPlanExercise> replacePlanExercises(
            Long workoutPlanId,
            List<WorkoutPlanExerciseRequestDTO> requests,
            String username) {

        WorkoutPlan workoutPlan =
                findOwnedPlan(workoutPlanId, username);

        List<WorkoutPlanExercise> newExercises = new ArrayList<>();

        for (int index = 0; index < requests.size(); index++) {

            WorkoutPlanExerciseRequestDTO request = requests.get(index);

            Exercise exercise =
                    findVisibleExercise(request.getExerciseId(), workoutPlan);

            newExercises.add(
                    buildConfiguredExercise(
                            workoutPlan,
                            exercise,
                            request,
                            index + 1));
        }

        List<WorkoutPlanExercise> oldExercises =
                workoutPlanExerciseRepository
                        .findByWorkoutPlanIdOrderByExerciseOrderAsc(
                                workoutPlanId);

        workoutPlan.getExercises().clear();
        workoutPlanExerciseRepository.deleteAll(oldExercises);
        workoutPlanExerciseRepository.flush();

        return workoutPlanExerciseRepository.saveAll(newExercises);
    }

    private WorkoutPlanExercise buildConfiguredExercise(
            WorkoutPlan workoutPlan,
            Exercise exercise,
            WorkoutPlanExerciseRequestDTO request,
            Integer exerciseOrder) {

        WorkoutPlanExercise workoutPlanExercise =
                new WorkoutPlanExercise();

        workoutPlanExercise.setWorkoutPlan(workoutPlan);
        workoutPlanExercise.setExercise(exercise);

        workoutPlanExercise.setExerciseOrder(exerciseOrder);

        workoutPlanExercise.setRestSeconds(
                request.getRestSeconds() != null
                        ? request.getRestSeconds()
                        : 15);

        /*
         * The exercise decides TIME or REPS (Plank = TIME, Push-Ups = REPS).
         * The requested type is only used to keep the planned target
         * when it matches; otherwise the exercise's default is used.
         */
        ExerciseTrackingType trackingType =
                ExerciseTracking.resolve(exercise);

        int target =
                ExerciseTracking.planTarget(
                        exercise,
                        request.getTrackingType(),
                        request.getTargetValue());

        workoutPlanExercise.setTrackingType(trackingType);
        workoutPlanExercise.setTargetValue(target);

        /*
         * duration_seconds is NOT NULL in the database: seconds for TIME
         * exercises, 0 for REPS exercises.
         */
        workoutPlanExercise.setDurationSeconds(
                trackingType == ExerciseTrackingType.TIME ? target : 0);

        Integer targetSets =
                request.getTargetSets();

        if (targetSets == null || targetSets <= 0) {
            targetSets = 1;
        }

        workoutPlanExercise.setTargetSets(targetSets);

        return workoutPlanExercise;
    }

    /*
     * ============================================================
     * GET ALL EXERCISES OF A PLAN
     * ============================================================
     */
    public List<WorkoutPlanExercise> getExercisesByWorkoutPlan(
            Long workoutPlanId,
            String username) {

        findOwnedPlan(workoutPlanId, username);

        return workoutPlanExerciseRepository
                .findByWorkoutPlanIdOrderByExerciseOrderAsc(
                        workoutPlanId);
    }

    /*
     * ============================================================
     * GET EXERCISE BY ID
     * ============================================================
     */
    public WorkoutPlanExercise getWorkoutPlanExerciseById(
            Long id,
            String username) {

        return findOwnedPlanExercise(id, username);
    }

    /*
     * ============================================================
     * DELETE EXERCISE FROM PLAN
     * ============================================================
     */
    public void deleteExerciseFromWorkoutPlan(
            Long id,
            String username) {

        workoutPlanExerciseRepository.delete(
                findOwnedPlanExercise(id, username));
    }

    /*
     * ============================================================
     * OWNERSHIP HELPERS
     * ============================================================
     *
     * Every read and write goes through these, so a user can only
     * see or change exercises of their own workout plans.
     */
    private WorkoutPlan findOwnedPlan(
            Long workoutPlanId,
            String username) {

        WorkoutPlan workoutPlan =
                workoutPlanRepository.findById(workoutPlanId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Workout plan not found with id: "
                                        + workoutPlanId));

        if (!workoutPlan.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You do not have permission to access this workout plan");
        }

        return workoutPlan;
    }

    private WorkoutPlanExercise findOwnedPlanExercise(
            Long id,
            String username) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Workout plan exercise not found with id: "
                                        + id));

        findOwnedPlan(
                workoutPlanExercise.getWorkoutPlan().getId(),
                username);

        return workoutPlanExercise;
    }

    /*
     * Built-in exercises, or custom exercises created by the plan owner.
     */
    private Exercise findVisibleExercise(
            Long exerciseId,
            WorkoutPlan workoutPlan) {

        return exerciseRepository
                .findVisibleById(
                        exerciseId,
                        workoutPlan.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exercise not found with id: "
                                + exerciseId));
    }
}
