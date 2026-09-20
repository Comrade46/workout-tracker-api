package com.workouttracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workouttracker.dto.WorkoutPlanExerciseResponse;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.service.WorkoutPlanExerciseService;

@RestController
@RequestMapping("/api/workout-plan-exercises")
public class WorkoutPlanExerciseController {

    private final WorkoutPlanExerciseService workoutPlanExerciseService;

    public WorkoutPlanExerciseController(
            WorkoutPlanExerciseService workoutPlanExerciseService) {

        this.workoutPlanExerciseService = workoutPlanExerciseService;
    }

    @PostMapping
    public ResponseEntity<WorkoutPlanExerciseResponse> addExerciseToWorkoutPlan(
            @RequestParam Long workoutPlanId,
            @RequestParam Long exerciseId,
            @RequestParam Integer durationSeconds,
            @RequestParam Integer restSeconds,
            @RequestParam Integer exerciseOrder) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseService.addExerciseToWorkoutPlan(
                        workoutPlanId,
                        exerciseId,
                        durationSeconds,
                        restSeconds,
                        exerciseOrder);

        WorkoutPlanExerciseResponse response =
                convertToResponse(workoutPlanExercise);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Existing endpoint.
     *
     * Used by the workout-plan-exercise API directly.
     */
    @GetMapping("/plan/{workoutPlanId}")
    public ResponseEntity<List<WorkoutPlanExerciseResponse>> getExercisesByWorkoutPlan(
            @PathVariable Long workoutPlanId) {

        List<WorkoutPlanExercise> exercises =
                workoutPlanExerciseService.getExercisesByWorkoutPlan(
                        workoutPlanId);

        List<WorkoutPlanExerciseResponse> responses =
                exercises.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /*
     * Workout Player endpoint.
     *
     * Frontend WorkoutPlayer.jsx calls:
     *
     * GET /api/workout-plan-exercises/workout-plans/{workoutPlanId}/exercises
     *
     * The existing service already contains the correct logic,
     * so this endpoint reuses the existing method instead of
     * duplicating service/database logic.
     */
    @GetMapping("/workout-plans/{workoutPlanId}/exercises")
    public ResponseEntity<List<WorkoutPlanExerciseResponse>> getExercisesForWorkoutPlayer(
            @PathVariable Long workoutPlanId) {

        return getExercisesByWorkoutPlan(workoutPlanId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanExerciseResponse> getWorkoutPlanExerciseById(
            @PathVariable Long id) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseService
                        .getWorkoutPlanExerciseById(id);

        WorkoutPlanExerciseResponse response =
                convertToResponse(workoutPlanExercise);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseFromWorkoutPlan(
            @PathVariable Long id) {

        workoutPlanExerciseService
                .deleteExerciseFromWorkoutPlan(id);

        return ResponseEntity.noContent().build();
    }

    private WorkoutPlanExerciseResponse convertToResponse(
            WorkoutPlanExercise workoutPlanExercise) {

        WorkoutPlanExerciseResponse response =
                new WorkoutPlanExerciseResponse();

        response.setId(workoutPlanExercise.getId());

        response.setWorkoutPlanId(
                workoutPlanExercise
                        .getWorkoutPlan()
                        .getId());

        response.setExerciseId(
                workoutPlanExercise
                        .getExercise()
                        .getId());

        response.setExerciseName(
                workoutPlanExercise
                        .getExercise()
                        .getName());

        response.setCategory(
                workoutPlanExercise
                        .getExercise()
                        .getCategory());

        response.setWorkoutType(
                workoutPlanExercise
                        .getExercise()
                        .getWorkoutType()
                        .toString());

        response.setEquipment(
                workoutPlanExercise
                        .getExercise()
                        .getEquipment());

        response.setDurationSeconds(
                workoutPlanExercise
                        .getDurationSeconds());

        response.setRestSeconds(
                workoutPlanExercise
                        .getRestSeconds());

        response.setExerciseOrder(
                workoutPlanExercise
                        .getExerciseOrder());

        return response;
    }
}