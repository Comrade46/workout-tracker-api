package com.workouttracker.service;

import com.workouttracker.dto.WorkoutSessionRequestDTO;
import com.workouttracker.dto.WorkoutSessionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutService {

    // Store the "How did it feel?" answer for a saved workout
    WorkoutSessionResponseDTO setFeeling(String clientId, String feeling, Long userId);

    // Workout already saved with this phone-made ID, or null
    WorkoutSessionResponseDTO findByClientId(String clientId, Long userId);

    WorkoutSessionResponseDTO logWorkout(
            WorkoutSessionRequestDTO requestDTO,
            Long userId
    );

    WorkoutSessionResponseDTO getWorkoutById(
            Long id,
            Long userId
    );

    List<WorkoutSessionResponseDTO> getAllWorkouts(
            Long userId
    );

    List<WorkoutSessionResponseDTO> getWorkoutsByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Long userId
    );

    void deleteWorkout(
            Long id,
            Long userId
    );
}