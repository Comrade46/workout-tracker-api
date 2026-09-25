package com.workouttracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttracker.model.WorkoutPlanExercise;

public interface WorkoutPlanExerciseRepository
        extends JpaRepository<WorkoutPlanExercise, Long> {

    List<WorkoutPlanExercise> findByWorkoutPlanIdOrderByExerciseOrderAsc(
            Long workoutPlanId
    );

    List<WorkoutPlanExercise> findByExerciseId(Long exerciseId);

    WorkoutPlanExercise findByWorkoutPlanIdAndExerciseOrder(
            Long workoutPlanId,
            Integer exerciseOrder
    );
}