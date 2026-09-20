package com.workouttracker.service;

import com.workouttracker.dto.AnalyticsSummaryDTO;
import com.workouttracker.dto.PersonalRecordDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.WorkoutSession;
import com.workouttracker.model.WorkoutSet;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutSessionRepository;
import com.workouttracker.repository.WorkoutSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutSetRepository setRepository;
    private final ExerciseRepository exerciseRepository;

    public AnalyticsServiceImpl(WorkoutSessionRepository sessionRepository,
                                WorkoutSetRepository setRepository,
                                ExerciseRepository exerciseRepository) {
        this.sessionRepository = sessionRepository;
        this.setRepository = setRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getUserAnalyticsSummary(Long userId) {
        List<WorkoutSession> sessions = sessionRepository.findByUserIdOrderByWorkoutDateDesc(userId);
        List<WorkoutSet> allSets = setRepository.findAllByUserId(userId);

        long totalWorkouts = sessions.size();
        long totalSets = allSets.size();

        int totalDuration = sessions.stream()
                .mapToInt(s -> s.getDurationMinutes() != null ? s.getDurationMinutes() : 0)
                .sum();

        BigDecimal totalVolume = allSets.stream()
                .map(s -> s.getWeight().multiply(BigDecimal.valueOf(s.getReps())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Breakdown by exercise category
        Map<String, Long> categoryCount = allSets.stream()
                .collect(Collectors.groupingBy(s -> s.getExercise().getCategory(), Collectors.counting()));

        List<PersonalRecordDTO> prs = getUserPersonalRecords(userId);

        return AnalyticsSummaryDTO.builder()
                .totalWorkouts(totalWorkouts)
                .totalSetsCompleted(totalSets)
                .totalVolumeLifted(totalVolume)
                .totalDurationMinutes(totalDuration)
                .workoutsByCategory(categoryCount)
                .topPersonalRecords(prs)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalRecordDTO> getUserPersonalRecords(Long userId) {
        List<WorkoutSet> allSets = setRepository.findAllByUserId(userId);

        // Group sets by exercise
        Map<Exercise, List<WorkoutSet>> setsByExercise = allSets.stream()
                .collect(Collectors.groupingBy(WorkoutSet::getExercise));

        List<PersonalRecordDTO> prList = new ArrayList<>();

        for (Map.Entry<Exercise, List<WorkoutSet>> entry : setsByExercise.entrySet()) {
            Exercise exercise = entry.getKey();
            List<WorkoutSet> sets = entry.getValue();

            // Find set with maximum weight (or max reps if weight is 0 for bodyweight)
            WorkoutSet bestSet = sets.stream()
                    .max(Comparator.comparing(WorkoutSet::getWeight)
                            .thenComparing(WorkoutSet::getReps))
                    .orElse(null);

            if (bestSet != null) {
                BigDecimal estimated1RM = calculate1RM(bestSet.getWeight(), bestSet.getReps());

                prList.add(PersonalRecordDTO.builder()
                        .exerciseId(exercise.getId())
                        .exerciseName(exercise.getName())
                        .category(exercise.getCategory())
                        .maxWeight(bestSet.getWeight())
                        .maxRepsAtMaxWeight(bestSet.getReps())
                        .estimatedOneRepMax(estimated1RM)
                        .achievedDate(bestSet.getSession().getWorkoutDate())
                        .build());
            }
        }

        return prList;
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalRecordDTO getExercisePR(Long userId, Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with ID: " + exerciseId));

        List<WorkoutSet> sets = setRepository.findUserSetsByExerciseOrdered(userId, exerciseId);

        if (sets.isEmpty()) {
            return PersonalRecordDTO.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .category(exercise.getCategory())
                    .maxWeight(BigDecimal.ZERO)
                    .maxRepsAtMaxWeight(0)
                    .estimatedOneRepMax(BigDecimal.ZERO)
                    .build();
        }

        WorkoutSet bestSet = sets.get(0);
        BigDecimal estimated1RM = calculate1RM(bestSet.getWeight(), bestSet.getReps());

        return PersonalRecordDTO.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .category(exercise.getCategory())
                .maxWeight(bestSet.getWeight())
                .maxRepsAtMaxWeight(bestSet.getReps())
                .estimatedOneRepMax(estimated1RM)
                .achievedDate(bestSet.getSession().getWorkoutDate())
                .build();
    }

    /**
     * Brzycki Formula: 1RM = Weight * (36 / (37 - reps))
     * Only valid for reps <= 36. For 1 rep, 1RM = Weight.
     */
    private BigDecimal calculate1RM(BigDecimal weight, int reps) {
        if (weight.compareTo(BigDecimal.ZERO) == 0 || reps <= 0) {
            return BigDecimal.ZERO;
        }
        if (reps == 1) {
            return weight;
        }
        if (reps >= 37) {
            reps = 36; // Guard against division by zero
        }
        double factor = 36.0 / (37.0 - reps);
        double oneRepMax = weight.doubleValue() * factor;
        return BigDecimal.valueOf(oneRepMax).setScale(2, RoundingMode.HALF_UP);
    }
}