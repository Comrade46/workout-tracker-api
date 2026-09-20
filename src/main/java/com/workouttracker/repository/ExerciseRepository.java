package com.workouttracker.repository;

import com.workouttracker.model.Exercise;
import com.workouttracker.model.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Find all exercises filtered by workout type (GYM, HOME, BOTH).
     */
    List<Exercise> findByWorkoutType(WorkoutType workoutType);

    /**
     * Find all exercises matching either a specific workout type or BOTH.
     * Useful for home users who can also perform universal bodyweight exercises.
     */
    List<Exercise> findByWorkoutTypeIn(List<WorkoutType> workoutTypes);

    /**
     * Find all exercises by category (e.g., Chest, Back, Legs) ignoring case.
     */
    List<Exercise> findByCategoryIgnoreCase(String category);

    /**
     * Find exercises by category and workout type.
     */
    List<Exercise> findByCategoryIgnoreCaseAndWorkoutType(String category, WorkoutType workoutType);

    /**
     * Search exercises whose name contains a keyword (case-insensitive).
     * Example: search "press" finds "Barbell Bench Press", "Incline Dumbbell Press", etc.
     */
    List<Exercise> findByNameContainingIgnoreCase(String keyword);
}