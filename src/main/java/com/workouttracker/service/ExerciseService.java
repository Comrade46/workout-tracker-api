package com.workouttracker.service;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.model.WorkoutType;

import java.util.List;

public interface ExerciseService {

    ExerciseResponseDTO createExercise(ExerciseRequestDTO requestDTO);

    ExerciseResponseDTO getExerciseById(Long id);

    List<ExerciseResponseDTO> getAllExercises();

    List<ExerciseResponseDTO> getExercisesByWorkoutType(WorkoutType workoutType);

    List<ExerciseResponseDTO> getExercisesByCategory(String category);

    List<ExerciseResponseDTO> searchExercisesByName(String keyword);

    ExerciseResponseDTO updateExercise(Long id, ExerciseRequestDTO requestDTO);

    void deleteExercise(Long id);
}
