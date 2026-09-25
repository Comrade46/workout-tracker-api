package com.workouttracker.controller;

import com.workouttracker.dto.WorkoutSessionRequestDTO;
import com.workouttracker.dto.WorkoutSessionResponseDTO;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.WorkoutService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workout-sessions")
public class WorkoutSessionController {

    private final WorkoutService workoutService;

    public WorkoutSessionController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // Create a workout session for the currently logged-in user
    @PostMapping
    public ResponseEntity<WorkoutSessionResponseDTO> logWorkout(
            @Valid @RequestBody WorkoutSessionRequestDTO requestDTO,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        WorkoutSessionResponseDTO response =
                workoutService.logWorkout(requestDTO, userId);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // Get workout by ID
    // Only the currently logged-in user's workout can be accessed
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponseDTO> getWorkoutById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        WorkoutSessionResponseDTO response =
                workoutService.getWorkoutById(id, userId);

        return ResponseEntity.ok(response);
    }

    // Get all workouts for the currently logged-in user
    @GetMapping
    public ResponseEntity<List<WorkoutSessionResponseDTO>> getAllWorkouts(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<WorkoutSessionResponseDTO> workouts =
                workoutService.getAllWorkouts(userId);

        return ResponseEntity.ok(workouts);
    }

    // Get workouts between two dates for the currently logged-in user
    @GetMapping("/date-range")
    public ResponseEntity<List<WorkoutSessionResponseDTO>> getWorkoutsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        List<WorkoutSessionResponseDTO> workouts =
                workoutService.getWorkoutsByDateRange(
                        startDate,
                        endDate,
                        userId
                );

        return ResponseEntity.ok(workouts);
    }

    // Delete workout
    // Only the currently logged-in user's workout can be deleted
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkout(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        workoutService.deleteWorkout(id, userId);

        return ResponseEntity.ok(
                "Workout session deleted successfully"
        );
    }

    /**
     * Gets the database ID of the currently authenticated user.
     *
     * JWT -> AuthTokenFilter -> UserDetailsImpl -> user ID
     */
    private Long getAuthenticatedUserId(Authentication authentication) {

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof UserDetailsImpl)) {

            throw new IllegalStateException(
                    "Authenticated user information is not available"
            );
        }

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        return userDetails.getId();
    }
}