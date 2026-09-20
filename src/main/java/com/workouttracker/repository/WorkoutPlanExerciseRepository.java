
package com.workouttracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workouttracker.model.WorkoutPlanExercise;

public interface WorkoutPlanExerciseRepository
        extends JpaRepository<WorkoutPlanExercise, Long> {

    List<WorkoutPlanExercise> findByWorkoutPlanIdOrderByExerciseOrderAsc(
            Long workoutPlanId
    );
}
