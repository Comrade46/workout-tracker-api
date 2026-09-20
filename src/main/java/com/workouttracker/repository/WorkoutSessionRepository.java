package com.workouttracker.repository;

import com.workouttracker.model.WorkoutSession;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository
        extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserIdOrderByWorkoutDateDesc(
            Long userId
    );

    List<WorkoutSession> findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /*
     * Used to protect individual workout sessions.
     *
     * The session is returned only when BOTH:
     * - session ID matches
     * - user ID matches
     */
    Optional<WorkoutSession> findByIdAndUserId(
            Long id,
            Long userId
    );
}