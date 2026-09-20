package com.workouttracker.repository;

import com.workouttracker.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    List<WorkoutSet> findBySessionId(Long sessionId);

    List<WorkoutSet> findByExerciseId(Long exerciseId);

    /**
     * Fetch all sets for a specific exercise across all sessions of a user,
     * ordered by weight descending and reps descending to find PRs quickly.
     */
    @Query("SELECT s FROM WorkoutSet s WHERE s.session.userId = :userId AND s.exercise.id = :exerciseId ORDER BY s.weight DESC, s.reps DESC")
    List<WorkoutSet> findUserSetsByExerciseOrdered(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);

    /**
     * Fetch all sets for a user across all sessions.
     */
    @Query("SELECT s FROM WorkoutSet s WHERE s.session.userId = :userId")
    List<WorkoutSet> findAllByUserId(@Param("userId") Long userId);
}