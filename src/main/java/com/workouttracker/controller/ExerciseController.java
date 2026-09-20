package com.workouttracker.controller;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.model.WorkoutType;
import com.workouttracker.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "*")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDTO>> getAllExercises(
            @RequestParam(required = false) WorkoutType workoutType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        if (workoutType != null) {
            return ResponseEntity.ok(exerciseService.getExercisesByWorkoutType(workoutType));
        }
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(exerciseService.getExercisesByCategory(category));
        }
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(exerciseService.searchExercisesByName(search));
        }
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> getExerciseById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDTO> createExercise(
            @Valid @RequestBody ExerciseRequestDTO requestDTO) {
        ExerciseResponseDTO created = exerciseService.createExercise(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDTO> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequestDTO requestDTO) {
        ExerciseResponseDTO updated = exerciseService.updateExercise(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}