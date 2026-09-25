package com.workouttracker.controller;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.model.WorkoutType;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDTO>> getAllExercises(
            @RequestParam(required = false) WorkoutType workoutType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetailsImpl user) {

        if (workoutType != null) {
            return ResponseEntity.ok(exerciseService.getExercisesByWorkoutType(workoutType, user));
        }
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(exerciseService.getExercisesByCategory(category.trim(), user));
        }
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(exerciseService.searchExercisesByName(search.trim(), user));
        }
        return ResponseEntity.ok(exerciseService.getAllExercises(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> getExerciseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id, user));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDTO> createExercise(
            @Valid @RequestBody ExerciseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetailsImpl user) {
        ExerciseResponseDTO created = exerciseService.createExercise(requestDTO, user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetailsImpl user) {
        ExerciseResponseDTO updated = exerciseService.updateExercise(id, requestDTO, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl user) {
        exerciseService.deleteExercise(id, user);
        return ResponseEntity.noContent().build();
    }
}
