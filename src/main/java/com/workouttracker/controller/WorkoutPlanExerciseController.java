package com.workouttracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workouttracker.dto.WorkoutPlanExerciseRequestDTO;
import com.workouttracker.dto.WorkoutPlanExerciseResponse;
import com.workouttracker.dto.WorkoutPlanExercisesReplaceRequestDTO;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.ExerciseTrackingType;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.service.ExerciseTracking;

import jakarta.validation.Valid;
import com.workouttracker.service.WorkoutPlanExerciseService;

@RestController
@RequestMapping("/api/workout-plan-exercises")
public class WorkoutPlanExerciseController {

    private final WorkoutPlanExerciseService workoutPlanExerciseService;

    public WorkoutPlanExerciseController(
            WorkoutPlanExerciseService workoutPlanExerciseService) {

        this.workoutPlanExerciseService =
                workoutPlanExerciseService;
    }

    @PostMapping
    public ResponseEntity<WorkoutPlanExerciseResponse>
    addExerciseToWorkoutPlan(
            @RequestParam Long workoutPlanId,
            @RequestParam Long exerciseId,
            @RequestParam Integer durationSeconds,
            @RequestParam Integer restSeconds,
            @RequestParam Integer exerciseOrder,
            Authentication authentication) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseService
                        .addExerciseToWorkoutPlan(
                                workoutPlanId,
                                exerciseId,
                                durationSeconds,
                                restSeconds,
                                exerciseOrder,
                                authentication.getName());

        WorkoutPlanExerciseResponse response =
                convertToResponse(workoutPlanExercise);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/configured")
    public ResponseEntity<WorkoutPlanExerciseResponse>
    addConfiguredExercise(
            @Valid @RequestBody WorkoutPlanExerciseRequestDTO request,
            Authentication authentication) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseService
                        .addConfiguredExercise(
                                request,
                                authentication.getName());

        WorkoutPlanExerciseResponse response =
                convertToResponse(workoutPlanExercise);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Replace all exercises of a plan in one transaction.
     * Only the plan owner can do this.
     */
    @PutMapping("/plan/{workoutPlanId}")
    public ResponseEntity<List<WorkoutPlanExerciseResponse>>
    replacePlanExercises(
            @PathVariable Long workoutPlanId,
            @Valid @RequestBody WorkoutPlanExercisesReplaceRequestDTO request,
            Authentication authentication) {

        List<WorkoutPlanExercise> saved =
                workoutPlanExerciseService
                        .replacePlanExercises(
                                workoutPlanId,
                                request.getExercises(),
                                authentication.getName());

        return ResponseEntity.ok(
                saved.stream()
                        .map(this::convertToResponse)
                        .toList());
    }

    @GetMapping("/plan/{workoutPlanId}")
    public ResponseEntity<List<WorkoutPlanExerciseResponse>>
    getExercisesByWorkoutPlan(
            @PathVariable Long workoutPlanId,
            Authentication authentication) {

        List<WorkoutPlanExercise> exercises =
                workoutPlanExerciseService
                        .getExercisesByWorkoutPlan(
                                workoutPlanId,
                                authentication.getName());

        List<WorkoutPlanExerciseResponse> responses =
                exercises.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping(
            "/workout-plans/{workoutPlanId}/exercises")
    public ResponseEntity<List<WorkoutPlanExerciseResponse>>
    getExercisesForWorkoutPlayer(
            @PathVariable Long workoutPlanId,
            Authentication authentication) {

        return getExercisesByWorkoutPlan(
                workoutPlanId,
                authentication);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanExerciseResponse>
    getWorkoutPlanExerciseById(
            @PathVariable Long id,
            Authentication authentication) {

        WorkoutPlanExercise workoutPlanExercise =
                workoutPlanExerciseService
                        .getWorkoutPlanExerciseById(
                                id,
                                authentication.getName());

        WorkoutPlanExerciseResponse response =
                convertToResponse(workoutPlanExercise);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteExerciseFromWorkoutPlan(
            @PathVariable Long id,
            Authentication authentication) {

        workoutPlanExerciseService
                .deleteExerciseFromWorkoutPlan(
                        id,
                        authentication.getName());

        return ResponseEntity
                .noContent()
                .build();
    }

    private WorkoutPlanExerciseResponse convertToResponse(
            WorkoutPlanExercise workoutPlanExercise) {

        WorkoutPlanExerciseResponse response =
                new WorkoutPlanExerciseResponse();

        response.setId(
                workoutPlanExercise.getId());

        response.setWorkoutPlanId(
                workoutPlanExercise
                        .getWorkoutPlan()
                        .getId());

        response.setWorkoutPlanName(
                workoutPlanExercise
                        .getWorkoutPlan()
                        .getName());

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

        if (workoutPlanExercise
                .getExercise()
                .getWorkoutType() != null) {

            response.setWorkoutType(
                    workoutPlanExercise
                            .getExercise()
                            .getWorkoutType()
                            .toString());
        }

        response.setEquipment(
                workoutPlanExercise
                        .getExercise()
                        .getEquipment());

        response.setExerciseOrder(
                workoutPlanExercise
                        .getExerciseOrder());

        response.setDurationSeconds(
                workoutPlanExercise
                        .getDurationSeconds());

        response.setRestSeconds(
                workoutPlanExercise
                        .getRestSeconds());

        /*
         * TIME / REPS always follows the exercise (Plank = TIME,
         * Russian Twists = REPS). Rows saved before this rule - with no
         * type or the wrong type - are corrected here, and their target
         * falls back to the exercise's default seconds / reps.
         */
        Exercise exercise = workoutPlanExercise.getExercise();

        ExerciseTrackingType trackingType =
                ExerciseTracking.resolve(exercise);

        response.setTrackingType(trackingType.toString());

        response.setTargetValue(
                ExerciseTracking.planTarget(
                        exercise,
                        workoutPlanExercise.getTrackingType(),
                        workoutPlanExercise.getTargetValue()));

        Integer targetSets = workoutPlanExercise.getTargetSets();

        response.setTargetSets(
                targetSets == null || targetSets < 1 ? 1 : targetSets);

        return response;
    }
}