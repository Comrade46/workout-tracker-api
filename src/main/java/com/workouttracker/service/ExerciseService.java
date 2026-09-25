package com.workouttracker.service;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.model.WorkoutType;
import com.workouttracker.security.UserDetailsImpl;

import java.util.List;

/*
 * Every method takes the logged-in user:
 * - users see built-in exercises plus their own custom ones
 * - users can edit / delete only their own custom exercises
 * - admins can also edit / delete built-in exercises
 */
public interface ExerciseService {

    ExerciseResponseDTO createExercise(ExerciseRequestDTO requestDTO, UserDetailsImpl user);

    ExerciseResponseDTO getExerciseById(Long id, UserDetailsImpl user);

    List<ExerciseResponseDTO> getAllExercises(UserDetailsImpl user);

    List<ExerciseResponseDTO> getExercisesByWorkoutType(WorkoutType workoutType, UserDetailsImpl user);

    List<ExerciseResponseDTO> getExercisesByCategory(String category, UserDetailsImpl user);

    List<ExerciseResponseDTO> searchExercisesByName(String keyword, UserDetailsImpl user);

    ExerciseResponseDTO updateExercise(Long id, ExerciseRequestDTO requestDTO, UserDetailsImpl user);

    void deleteExercise(Long id, UserDetailsImpl user);
}
