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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final int MAX_SESSION_MINUTES = 24 * 60;

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutSetRepository setRepository;
    private final ExerciseRepository exerciseRepository;

    public AnalyticsServiceImpl(
            WorkoutSessionRepository sessionRepository,
            WorkoutSetRepository setRepository,
            ExerciseRepository exerciseRepository) {

        this.sessionRepository = sessionRepository;
        this.setRepository = setRepository;
        this.exerciseRepository = exerciseRepository;
    }

    // =========================================================
    // ANALYTICS SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getUserAnalyticsSummary(Long userId) {

        List<WorkoutSession> sessions =
                sessionRepository.findByUserIdOrderByWorkoutDateDesc(userId);

        List<WorkoutSet> allSets =
                setRepository.findAllByUserId(userId);

        long totalWorkouts = sessions.size();

        long totalSets = allSets.size();

        // Durations over 24 hours are impossible (bad data) and are ignored.
        int totalDuration = sessions.stream()
                .mapToInt(session ->
                        session.getDurationMinutes() != null
                                && session.getDurationMinutes() >= 0
                                && session.getDurationMinutes() <= MAX_SESSION_MINUTES
                                ? session.getDurationMinutes()
                                : 0)
                .sum();

        /*
         * Only REPS sets contribute to weight volume.
         *
         * TIME exercises such as Plank, Wall Sit, etc.
         * must not contribute to kg volume.
         */
        BigDecimal totalVolume = allSets.stream()
                .filter(this::isRepBasedSet)
                .map(this::calculateSetVolume)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        Map<String, Long> categoryCount =
                allSets.stream()
                        .filter(Objects::nonNull)
                        .filter(set ->
                                set.getExercise() != null)
                        .collect(
                                Collectors.groupingBy(
                                        set -> {

                                            String category =
                                                    set.getExercise()
                                                            .getCategory();

                                            return category != null
                                                    && !category
                                                    .trim()
                                                    .isEmpty()
                                                    ? category
                                                    : "Other";
                                        },
                                        Collectors.counting()
                                )
                        );

        // Reuse the sets already loaded above instead of querying again.
        List<PersonalRecordDTO> prs =
                buildPersonalRecords(allSets);

        return AnalyticsSummaryDTO.builder()
                .totalWorkouts(totalWorkouts)
                .totalSetsCompleted(totalSets)
                .totalVolumeLifted(totalVolume)
                .totalDurationMinutes(totalDuration)
                .workoutsByCategory(categoryCount)
                .topPersonalRecords(prs)
                .build();
    }

    // =========================================================
    // ALL PERSONAL RECORDS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<PersonalRecordDTO> getUserPersonalRecords(
            Long userId) {

        return buildPersonalRecords(
                setRepository.findAllByUserId(userId));
    }

    private List<PersonalRecordDTO> buildPersonalRecords(
            List<WorkoutSet> allSets) {

        Map<Exercise, List<WorkoutSet>> setsByExercise =
                allSets.stream()
                        .filter(Objects::nonNull)
                        .filter(set ->
                                set.getExercise() != null)
                        .collect(
                                Collectors.groupingBy(
                                        WorkoutSet::getExercise
                                )
                        );

        List<PersonalRecordDTO> prList =
                new ArrayList<>();

        for (Map.Entry<Exercise, List<WorkoutSet>> entry
                : setsByExercise.entrySet()) {

            Exercise exercise = entry.getKey();

            List<WorkoutSet> sets = entry.getValue();

            if (exercise == null
                    || sets == null
                    || sets.isEmpty()) {
                continue;
            }

            /*
             * IMPORTANT:
             *
             * TIME is checked FIRST.
             *
             * If durationSeconds exists, the set is treated
             * as a TIME set even if an old/extra reps value
             * happens to exist.
             */
            List<WorkoutSet> timeSets =
                    sets.stream()
                            .filter(this::isTimeBasedSet)
                            .toList();

            List<WorkoutSet> repSets =
                    sets.stream()
                            .filter(this::isRepBasedSet)
                            .filter(set ->
                                    !isTimeBasedSet(set))
                            .toList();

            // =================================================
            // TIME PR
            // =================================================

            if (!timeSets.isEmpty()) {

                WorkoutSet bestTimeSet =
                        timeSets.stream()
                                .max(
                                        Comparator.comparing(
                                                this::safeDuration
                                        )
                                )
                                .orElse(null);

                if (bestTimeSet != null) {

                    PersonalRecordDTO timePR =
                            PersonalRecordDTO.builder()
                                    .exerciseId(
                                            exercise.getId()
                                    )
                                    .exerciseName(
                                            exercise.getName()
                                    )
                                    .category(
                                            exercise.getCategory()
                                    )
                                    .trackingType("TIME")
                                    .maxWeight(
                                            BigDecimal.ZERO
                                    )
                                    .maxRepsAtMaxWeight(
                                            0
                                    )
                                    .estimatedOneRepMax(
                                            BigDecimal.ZERO
                                    )
                                    .bestDurationSeconds(
                                            safeDuration(
                                                    bestTimeSet
                                            )
                                    )
                                    .achievedDate(
                                            bestTimeSet
                                                    .getSession()
                                                    .getWorkoutDate()
                                    )
                                    .build();

                    prList.add(timePR);
                }

                /*
                 * If this exercise contains TIME sets,
                 * we do not allow the same exercise to also
                 * appear as a REPS PR.
                 *
                 * This prevents Plank Hold from showing:
                 *
                 * 0 kg
                 * 5 reps
                 * 0 volume
                 *
                 * alongside its TIME record.
                 */
                continue;
            }

            // =================================================
            // REPS PR
            // =================================================

            if (!repSets.isEmpty()) {

                WorkoutSet bestSet =
                        repSets.stream()
                                .max(
                                        Comparator
                                                .comparing(
                                                        this::safeWeight
                                                )
                                                .thenComparing(
                                                        this::safeReps
                                                )
                                )
                                .orElse(null);

                if (bestSet != null) {

                    BigDecimal weight =
                            safeWeight(bestSet);

                    int reps =
                            safeReps(bestSet);

                    BigDecimal estimated1RM =
                            calculate1RM(
                                    weight,
                                    reps
                            );

                    PersonalRecordDTO repsPR =
                            PersonalRecordDTO.builder()
                                    .exerciseId(
                                            exercise.getId()
                                    )
                                    .exerciseName(
                                            exercise.getName()
                                    )
                                    .category(
                                            exercise.getCategory()
                                    )
                                    .trackingType("REPS")
                                    .maxWeight(weight)
                                    .maxRepsAtMaxWeight(
                                            reps
                                    )
                                    .estimatedOneRepMax(
                                            estimated1RM
                                    )
                                    .bestDurationSeconds(
                                            0
                                    )
                                    .achievedDate(
                                            bestSet
                                                    .getSession()
                                                    .getWorkoutDate()
                                    )
                                    .build();

                    prList.add(repsPR);
                }
            }
        }

        return prList;
    }

    // =========================================================
    // SINGLE EXERCISE PR
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PersonalRecordDTO getExercisePR(
            Long userId,
            Long exerciseId) {

        Exercise exercise =
                exerciseRepository.findVisibleById(exerciseId, userId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Exercise not found with ID: "
                                                        + exerciseId
                                        )
                        );

        List<WorkoutSet> sets =
                setRepository.findUserSetsByExerciseOrdered(
                        userId,
                        exerciseId
                );

        if (sets.isEmpty()) {

            return PersonalRecordDTO.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .category(exercise.getCategory())
                    .trackingType("REPS")
                    .maxWeight(BigDecimal.ZERO)
                    .maxRepsAtMaxWeight(0)
                    .estimatedOneRepMax(
                            BigDecimal.ZERO
                    )
                    .bestDurationSeconds(0)
                    .build();
        }

        /*
         * TIME ALWAYS HAS PRIORITY.
         */
        List<WorkoutSet> timeSets =
                sets.stream()
                        .filter(this::isTimeBasedSet)
                        .toList();

        if (!timeSets.isEmpty()) {

            WorkoutSet bestTimeSet =
                    timeSets.stream()
                            .max(
                                    Comparator.comparing(
                                            this::safeDuration
                                    )
                            )
                            .orElse(null);

            if (bestTimeSet != null) {

                return PersonalRecordDTO.builder()
                        .exerciseId(
                                exercise.getId()
                        )
                        .exerciseName(
                                exercise.getName()
                        )
                        .category(
                                exercise.getCategory()
                        )
                        .trackingType("TIME")
                        .maxWeight(
                                BigDecimal.ZERO
                        )
                        .maxRepsAtMaxWeight(0)
                        .estimatedOneRepMax(
                                BigDecimal.ZERO
                        )
                        .bestDurationSeconds(
                                safeDuration(
                                        bestTimeSet
                                )
                        )
                        .achievedDate(
                                bestTimeSet
                                        .getSession()
                                        .getWorkoutDate()
                        )
                        .build();
            }
        }

        // =====================================================
        // REPS PR
        // =====================================================

        List<WorkoutSet> repSets =
                sets.stream()
                        .filter(this::isRepBasedSet)
                        .filter(set ->
                                !isTimeBasedSet(set))
                        .toList();

        if (!repSets.isEmpty()) {

            WorkoutSet bestSet =
                    repSets.stream()
                            .max(
                                    Comparator
                                            .comparing(
                                                    this::safeWeight
                                            )
                                            .thenComparing(
                                                    this::safeReps
                                            )
                            )
                            .orElse(null);

            if (bestSet != null) {

                BigDecimal weight =
                        safeWeight(bestSet);

                int reps =
                        safeReps(bestSet);

                return PersonalRecordDTO.builder()
                        .exerciseId(
                                exercise.getId()
                        )
                        .exerciseName(
                                exercise.getName()
                        )
                        .category(
                                exercise.getCategory()
                        )
                        .trackingType("REPS")
                        .maxWeight(weight)
                        .maxRepsAtMaxWeight(reps)
                        .estimatedOneRepMax(
                                calculate1RM(
                                        weight,
                                        reps
                                )
                        )
                        .bestDurationSeconds(0)
                        .achievedDate(
                                bestSet
                                        .getSession()
                                        .getWorkoutDate()
                        )
                        .build();
            }
        }

        // =====================================================
        // EMPTY PR
        // =====================================================

        return PersonalRecordDTO.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .category(exercise.getCategory())
                .trackingType("REPS")
                .maxWeight(BigDecimal.ZERO)
                .maxRepsAtMaxWeight(0)
                .estimatedOneRepMax(
                        BigDecimal.ZERO
                )
                .bestDurationSeconds(0)
                .build();
    }

    // =========================================================
    // SET TYPE DETECTION
    // =========================================================

    /*
     * A set is TIME-based when durationSeconds is greater
     * than zero.
     */
    private boolean isTimeBasedSet(WorkoutSet set) {

        return set != null
                && set.getDurationSeconds() != null
                && set.getDurationSeconds() > 0;
    }

    /*
     * A REPS set is valid only when:
     *
     * reps > 0
     *
     * AND it is NOT a TIME set.
     */
    private boolean isRepBasedSet(WorkoutSet set) {

        return set != null
                && set.getReps() != null
                && set.getReps() > 0
                && !isTimeBasedSet(set);
    }

    // =========================================================
    // SAFE VALUES
    // =========================================================

    private BigDecimal safeWeight(
            WorkoutSet set) {

        if (set == null
                || set.getWeight() == null) {

            return BigDecimal.ZERO;
        }

        return set.getWeight();
    }

    private int safeReps(
            WorkoutSet set) {

        if (set == null
                || set.getReps() == null) {

            return 0;
        }

        return set.getReps();
    }

    private int safeDuration(
            WorkoutSet set) {

        if (set == null
                || set.getDurationSeconds() == null) {

            return 0;
        }

        return set.getDurationSeconds();
    }

    // =========================================================
    // VOLUME
    // =========================================================

    private BigDecimal calculateSetVolume(
            WorkoutSet set) {

        BigDecimal weight =
                safeWeight(set);

        int reps =
                safeReps(set);

        return weight.multiply(
                BigDecimal.valueOf(reps)
        );
    }

    // =========================================================
    // ONE REP MAX
    // =========================================================

    private BigDecimal calculate1RM(
            BigDecimal weight,
            int reps) {

        if (weight == null
                || weight.compareTo(
                        BigDecimal.ZERO
                ) == 0
                || reps <= 0) {

            return BigDecimal.ZERO;
        }

        if (reps == 1) {
            return weight;
        }

        if (reps >= 37) {
            reps = 36;
        }

        double factor =
                36.0 /
                        (37.0 - reps);

        double oneRepMax =
                weight.doubleValue()
                        * factor;

        return BigDecimal
                .valueOf(oneRepMax)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }
}