package com.workouttracker.service;

import com.workouttracker.dto.WorkoutSessionRequestDTO;
import com.workouttracker.dto.WorkoutSessionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutService {

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