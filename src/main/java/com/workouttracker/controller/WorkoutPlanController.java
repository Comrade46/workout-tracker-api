package com.workouttracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workouttracker.dto.WorkoutPlanRequestDTO;
import com.workouttracker.dto.WorkoutPlanResponse;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.User;
import com.workouttracker.model.WorkoutPlan;
import com.workouttracker.repository.UserRepository;
import com.workouttracker.service.WorkoutPlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final UserRepository userRepository;

    public WorkoutPlanController(
            WorkoutPlanService workoutPlanService,
            UserRepository userRepository) {

        this.workoutPlanService = workoutPlanService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(
            @Valid @RequestBody WorkoutPlanRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));

        WorkoutPlan createdPlan =
                workoutPlanService.createWorkoutPlan(
                        toEntity(request),
                        user);

        WorkoutPlanResponse response =
                convertToResponse(createdPlan);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutPlanResponse>> getMyWorkoutPlans(
            Authentication authentication) {

        String username = authentication.getName();

        List<WorkoutPlan> plans =
                workoutPlanService.getUserWorkoutPlans(username);

        List<WorkoutPlanResponse> responses =
                plans.stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlanById(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        WorkoutPlan plan =
                workoutPlanService.getWorkoutPlanById(id);

        if (!plan.getUser().getUsername().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        return ResponseEntity.ok(
                convertToResponse(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlan(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutPlanRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();

        WorkoutPlan existingPlan =
                workoutPlanService.getWorkoutPlanById(id);

        if (!existingPlan.getUser().getUsername().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        WorkoutPlan updated =
                workoutPlanService.updateWorkoutPlan(
                        id,
                        toEntity(request));

        return ResponseEntity.ok(
                convertToResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutPlan(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        WorkoutPlan existingPlan =
                workoutPlanService.getWorkoutPlanById(id);

        if (!existingPlan.getUser().getUsername().equals(username)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        workoutPlanService.deleteWorkoutPlan(id);

        return ResponseEntity.noContent().build();
    }

    /*
     * Only copies user-editable fields, so id / user / exercises
     * can never be set from the request body.
     */
    private WorkoutPlan toEntity(
            WorkoutPlanRequestDTO request) {

        WorkoutPlan plan = new WorkoutPlan();

        plan.setName(request.getName().trim());
        plan.setDescription(request.getDescription());
        plan.setCategory(request.getCategory());
        plan.setDifficulty(request.getDifficulty());

        return plan;
    }

    private WorkoutPlanResponse convertToResponse(
            WorkoutPlan plan) {

        return new WorkoutPlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getCategory(),
                plan.getDifficulty(),
                plan.getCreatedAt()
        );
    }
}