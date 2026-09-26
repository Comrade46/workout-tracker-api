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

        this.workoutSessionRepository =
                workoutSessionRepository;

        this.exerciseRepository =
                exerciseRepository;
    }

    // =========================================================
    // LOG WORKOUT
    // =========================================================

    @Override
    @Transactional
    public WorkoutSessionResponseDTO logWorkout(
            WorkoutSessionRequestDTO requestDTO,
            Long userId) {

        String clientId = normaliseClientId(requestDTO.getClientId());

        // Sent again from the phone's queue: return the saved workout.
        if (clientId != null) {
            WorkoutSession existing = workoutSessionRepository
                    .findByUserIdAndClientId(userId, clientId)
                    .orElse(null);

            if (existing != null) {
                return mapToResponseDTO(existing);
            }
        }

        WorkoutSession session =
                WorkoutSession.builder()
                        .userId(userId)
                        .workoutDate(
                                requestDTO.getWorkoutDate()
                        )
                        .notes(
                                requestDTO.getNotes()
                        )
                        .durationMinutes(
                                requestDTO.getDurationMinutes()
                        )
                        .build();

        for (WorkoutSetRequestDTO setDTO :
                requestDTO.getSets()) {

            Exercise exercise =
                    exerciseRepository
                            .findVisibleById(
                                    setDTO.getExerciseId(),
                                    userId
                            )
                            .orElseThrow(
                                    () ->
                                            new ResourceNotFoundException(
                                                    "Exercise not found with ID: "
                                                            + setDTO.getExerciseId()
                                            )
                            );

            WorkoutSet set =
                    WorkoutSet.builder()
                            .exercise(exercise)
                            .setNumber(
                                    setDTO.getSetNumber()
                            )
                            .weight(
                                    setDTO.getWeight()
                            )
                            .reps(
                                    setDTO.getReps()
                            )
                            .rpe(
                                    setDTO.getRpe()
                            )
                            .durationSeconds(
                                    setDTO.getDurationSeconds()
                            )
                            .build();

            session.addSet(set);
        }

        session.setClientId(clientId);
        session.setFeeling(requestDTO.getFeeling());
        session.setIntensity(requestDTO.getIntensity());

        WorkoutSession savedSession =
                workoutSessionRepository.save(
                        session
                );

        return mapToResponseDTO(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionResponseDTO findByClientId(
            String clientId,
            Long userId) {

        String id = normaliseClientId(clientId);

        return id == null
                ? null
                : workoutSessionRepository
                        .findByUserIdAndClientId(userId, id)
                        .map(this::mapToResponseDTO)
                        .orElse(null);
    }

    private static String normaliseClientId(String clientId) {
        return clientId == null || clientId.isBlank() ? null : clientId.trim();
    }

    // =========================================================
    // GET WORKOUT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public WorkoutSessionResponseDTO getWorkoutById(
            Long id,
            Long userId) {

        WorkoutSession session =
                workoutSessionRepository
                        .findByIdAndUserId(
                                id,
                                userId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Workout session not found with ID: "
                                                        + id
                                        )
                        );

        return mapToResponseDTO(session);
    }

    // =========================================================
    // GET ALL WORKOUTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutSessionResponseDTO> getAllWorkouts(
            Long userId) {

        return workoutSessionRepository
                .findByUserIdOrderByWorkoutDateDesc(
                        userId
                )
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================================================
    // GET WORKOUTS BY DATE RANGE
    // =========================================================

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

    // =========================================================
    // DELETE WORKOUT
    // =========================================================

    @Override
    @Transactional
    public void deleteWorkout(
            Long id,
            Long userId) {

        WorkoutSession session =
                workoutSessionRepository
                        .findByIdAndUserId(
                                id,
                                userId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Workout session not found with ID: "
                                                        + id
                                        )
                        );

        workoutSessionRepository.delete(
                session
        );
    }

    // =========================================================
    // MAP ENTITY → RESPONSE DTO
    // =========================================================

    private WorkoutSessionResponseDTO mapToResponseDTO(
            WorkoutSession session) {

        BigDecimal totalVolume =
                BigDecimal.ZERO;

        List<WorkoutSetResponseDTO> setDTOs =
                new ArrayList<>();

        if (session.getSets() != null) {

            for (WorkoutSet set :
                    session.getSets()) {

                Integer durationSeconds =
                        set.getDurationSeconds();

                boolean timeBased =
                        durationSeconds != null
                                && durationSeconds > 0;

                BigDecimal setVolume =
                        BigDecimal.ZERO;

                /*
                 * TIME SET
                 *
                 * Do not calculate:
                 *
                 * weight × reps
                 *
                 * for a TIME exercise.
                 */
                if (!timeBased) {

                    BigDecimal weight =
                            set.getWeight() != null
                                    ? set.getWeight()
                                    : BigDecimal.ZERO;

                    Integer reps =
                            set.getReps() != null
                                    ? set.getReps()
                                    : 0;

                    setVolume =
                            weight.multiply(
                                    BigDecimal.valueOf(
                                            reps
                                    )
                            );

                    totalVolume =
                            totalVolume.add(
                                    setVolume
                            );
                }

                WorkoutSetResponseDTO setDTO =
                        WorkoutSetResponseDTO
                                .builder()
                                .id(
                                        set.getId()
                                )
                                .exerciseId(
                                        set.getExercise()
                                                .getId()
                                )
                                .exerciseName(
                                        set.getExercise()
                                                .getName()
                                )
                                .category(
                                        set.getExercise()
                                                .getCategory()
                                )
                                .setNumber(
                                        set.getSetNumber()
                                )
                                .weight(
                                        set.getWeight()
                                )
                                .reps(
                                        set.getReps()
                                )
                                .rpe(
                                        set.getRpe()
                                )
                                .durationSeconds(
                                        durationSeconds
                                )
                                .volume(
                                        setVolume
                                )
                                .build();

                setDTOs.add(setDTO);
            }
        }

        WorkoutSessionResponseDTO response = WorkoutSessionResponseDTO
                .builder()
                .id(
                        session.getId()
                )
                .userId(
                        session.getUserId()
                )
                .workoutDate(
                        session.getWorkoutDate()
                )
                .notes(
                        session.getNotes()
                )
                .durationMinutes(
                        session.getDurationMinutes()
                )
                .totalVolume(
                        totalVolume
                )
                .totalSets(
                        setDTOs.size()
                )
                .createdAt(
                        session.getCreatedAt()
                )
                .sets(
                        setDTOs
                )
                .build();

        response.setFeeling(session.getFeeling());
        response.setIntensity(session.getIntensity());

        return response;
    }

    // "How did it feel?" answered after the workout was saved.
    @Override
    @Transactional
    public WorkoutSessionResponseDTO setFeeling(
            String clientId,
            String feeling,
            Long userId) {

        String id = normaliseClientId(clientId);

        WorkoutSession session = id == null
                ? null
                : workoutSessionRepository.findByUserIdAndClientId(userId, id).orElse(null);

        if (session == null) {
            throw new ResourceNotFoundException("Workout not found for this client ID");
        }

        session.setFeeling(feeling);

        return mapToResponseDTO(session);
    }
}