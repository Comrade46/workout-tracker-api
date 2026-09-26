package com.workouttracker.repository;

import com.workouttracker.model.WorkoutSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    // A workout already uploaded from the phone's queue (see clientId).
    Optional<WorkoutSession> findByUserIdAndClientId(
            Long userId,
            String clientId
    );

    // Admin overview: per user [userId, workout count, last workout date].
    @Query("select s.userId, count(s), max(s.workoutDate) from WorkoutSession s group by s.userId")
    List<Object[]> countByUser();
}