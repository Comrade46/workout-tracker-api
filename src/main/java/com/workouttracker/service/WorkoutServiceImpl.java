package com.workouttracker.service;

import com.workouttracker.dto.WorkoutSessionRequestDTO;
import com.workouttracker.dto.WorkoutSessionResponseDTO;
import com.workouttracker.dto.WorkoutSetRequestDTO;
import com.workouttracker.dto.WorkoutSetResponseDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.WorkoutSession;
import com.workouttracker.model.WorkoutSet;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutSessionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutServiceImpl(
            WorkoutSessionRepository workoutSessionRepository,
            ExerciseRepository exerciseRepository) {

        this.workoutSessionRepository = workoutSessionRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    @Transactional
    public WorkoutSessionResponseDTO logWorkout(
            WorkoutSessionRequestDTO requestDTO,
            Long userId) {

        // Create the parent WorkoutSession for the authenticated user
        WorkoutSession session = WorkoutSession.builder()
                .userId(userId)
                .workoutDate(requestDTO.getWorkoutDate())
                .notes(requestDTO.getNotes())
                .durationMinutes(requestDTO.getDurationMinutes())
                .build();

        // Build and attach each WorkoutSet child
        for (WorkoutSetRequestDTO setDTO : requestDTO.getSets()) {

            Exercise exercise = exerciseRepository
                    .findById(setDTO.getExerciseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Exercise not found with ID: "
                                            + setDTO.getExerciseId()
                            )
                    );

            WorkoutSet set = WorkoutSet.builder()
                    .exercise(exercise)
                    .setNumber(setDTO.getSetNumber())
                    .weight(setDTO.getWeight())
                    .reps(setDTO.getReps())
                    .rpe(setDTO.getRpe())
                    .build();

            session.addSet(set);
        }

        // Save parent session
        WorkoutSession savedSession =
                workoutSessionRepository.save(session);

        return mapToResponseDTO(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionResponseDTO getWorkoutById(
            Long id,
            Long userId) {

        /*
         * Important:
         * Find the workout only if it belongs to the
         * currently authenticated user.
         */
        WorkoutSession session =
                workoutSessionRepository
                        .findByIdAndUserId(id, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Workout session not found with ID: "
                                                + id
                                )
                        );

        return mapToResponseDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSessionResponseDTO> getAllWorkouts(
            Long userId) {

        return workoutSessionRepository
                .findByUserIdOrderByWorkoutDateDesc(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSessionResponseDTO> getWorkoutsByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Long userId) {

        return workoutSessionRepository
                .findByUserIdAndWorkoutDateBetweenOrderByWorkoutDateDesc(
                        userId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWorkout(
            Long id,
            Long userId) {

        /*
         * Delete only if the workout belongs to
         * the authenticated user.
         */
        WorkoutSession session =
                workoutSessionRepository
                        .findByIdAndUserId(id, userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Workout session not found with ID: "
                                                + id
                                )
                        );

        workoutSessionRepository.delete(session);
    }

    // =========================================================================
    // Helper: Map WorkoutSession Entity to Response DTO
    // =========================================================================

    private WorkoutSessionResponseDTO mapToResponseDTO(
            WorkoutSession session) {

        BigDecimal totalVolume = BigDecimal.ZERO;

        List<WorkoutSetResponseDTO> setDTOs =
                new ArrayList<>();

        if (session.getSets() != null) {

            for (WorkoutSet set : session.getSets()) {

                BigDecimal setVolume =
                        set.getWeight()
                                .multiply(
                                        BigDecimal.valueOf(
                                                set.getReps()
                                        )
                                );

                totalVolume =
                        totalVolume.add(setVolume);

                WorkoutSetResponseDTO setDTO =
                        WorkoutSetResponseDTO.builder()
                                .id(set.getId())
                                .exerciseId(
                                        set.getExercise().getId()
                                )
                                .exerciseName(
                                        set.getExercise().getName()
                                )
                                .category(
                                        set.getExercise().getCategory()
                                )
                                .setNumber(set.getSetNumber())
                                .weight(set.getWeight())
                                .reps(set.getReps())
                                .rpe(set.getRpe())
                                .volume(setVolume)
                                .build();

                setDTOs.add(setDTO);
            }
        }

        return WorkoutSessionResponseDTO.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .workoutDate(session.getWorkoutDate())
                .notes(session.getNotes())
                .durationMinutes(
                        session.getDurationMinutes()
                )
                .totalVolume(totalVolume)
                .totalSets(setDTOs.size())
                .createdAt(session.getCreatedAt())
                .sets(setDTOs)
                .build();
    }
}