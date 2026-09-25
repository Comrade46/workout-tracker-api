package com.workouttracker.service;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.exception.ResourceInUseException;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.ExerciseTrackingType;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.model.WorkoutType;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutPlanExerciseRepository;
import com.workouttracker.repository.WorkoutSetRepository;
import com.workouttracker.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    /*
     * Usernames allowed to edit / delete built-in exercises.
     * Configured with ADMIN_USERNAMES (comma separated).
     */
    private final Set<String> adminUsernames;

    private static final int DEFAULT_REPS = ExerciseTracking.DEFAULT_REPS;
    private static final int DEFAULT_DURATION_SECONDS = ExerciseTracking.DEFAULT_DURATION_SECONDS;
    private static final int DEFAULT_REST_SECONDS = 15;

    public ExerciseServiceImpl(
            ExerciseRepository exerciseRepository,
            WorkoutPlanExerciseRepository workoutPlanExerciseRepository,
            WorkoutSetRepository workoutSetRepository,
            @Value("${workouttracker.app.admin-usernames:}") String adminUsernames
    ) {
        this.exerciseRepository = exerciseRepository;
        this.workoutPlanExerciseRepository =
                workoutPlanExerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.adminUsernames = Arrays.stream(adminUsernames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional
    public ExerciseResponseDTO createExercise(
            ExerciseRequestDTO requestDTO,
            UserDetailsImpl user
    ) {

        Exercise exercise = Exercise.builder()
                .name(requestDTO.getName().trim())
                .category(requestDTO.getCategory().trim())
                .workoutType(requestDTO.getWorkoutType())
                .equipment(
                        requestDTO.getEquipment() == null
                                ? "No Equipment"
                                : requestDTO.getEquipment().trim()
                )
                .restSeconds(
                        requestDTO.getRestSeconds()
                )
                .isCustom(true)
                .build();

        // Custom exercises are private to the user who created them
        exercise.setCreatedBy(user.getId());

        applyTracking(exercise, requestDTO);

        Exercise savedExercise =
                exerciseRepository.save(exercise);

        return mapToResponseDTO(savedExercise, user);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponseDTO getExerciseById(
            Long id,
            UserDetailsImpl user
    ) {

        return mapToResponseDTO(findVisible(id, user), user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getAllExercises(
            UserDetailsImpl user
    ) {

        return findAllVisible(user, exercise -> true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getExercisesByWorkoutType(
            WorkoutType workoutType,
            UserDetailsImpl user
    ) {

        return findAllVisible(
                user,
                exercise -> exercise.getWorkoutType() == workoutType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getExercisesByCategory(
            String category,
            UserDetailsImpl user
    ) {

        return findAllVisible(
                user,
                exercise -> category.equalsIgnoreCase(exercise.getCategory())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> searchExercisesByName(
            String keyword,
            UserDetailsImpl user
    ) {

        String search = keyword.toLowerCase(Locale.ROOT);

        return findAllVisible(
                user,
                exercise -> exercise.getName() != null
                        && exercise.getName()
                        .toLowerCase(Locale.ROOT)
                        .contains(search)
        );
    }

    @Override
    @Transactional
    public ExerciseResponseDTO updateExercise(
            Long id,
            ExerciseRequestDTO requestDTO,
            UserDetailsImpl user
    ) {

        Exercise exercise = findEditable(id, user);

        exercise.setName(
                requestDTO.getName().trim()
        );

        exercise.setCategory(
                requestDTO.getCategory().trim()
        );

        exercise.setWorkoutType(
                requestDTO.getWorkoutType()
        );

        exercise.setEquipment(
                requestDTO.getEquipment() == null
                        ? "No Equipment"
                        : requestDTO.getEquipment().trim()
        );

        exercise.setRestSeconds(
                requestDTO.getRestSeconds()
        );

        applyTracking(exercise, requestDTO);

        Exercise updatedExercise =
                exerciseRepository.save(exercise);

        return mapToResponseDTO(updatedExercise, user);
    }

    @Override
    @Transactional
    public void deleteExercise(
            Long id,
            UserDetailsImpl user
    ) {

        Exercise exercise = findEditable(id, user);

        /*
         * Logged workout sets must keep their exercise,
         * otherwise history and personal records break.
         */
        if (workoutSetRepository.existsByExerciseId(id)) {
            throw new ResourceInUseException(
                    "\"" + exercise.getName() + "\" is used in workout history"
                            + " and cannot be deleted. You can edit it instead."
            );
        }

        /*
         * Remove the exercise from workout plans first.
         */
        List<WorkoutPlanExercise> planExercises =
                workoutPlanExerciseRepository
                        .findByExerciseId(id);

        if (!planExercises.isEmpty()) {

            workoutPlanExerciseRepository
                    .deleteAll(planExercises);
        }

        /*
         * Finally delete the exercise itself.
         */
        exerciseRepository.delete(exercise);
    }

    // =========================================================
    // ACCESS RULES
    // =========================================================

    private boolean isAdmin(UserDetailsImpl user) {
        return adminUsernames.contains(user.getUsername());
    }

    /*
     * Own custom exercises are always editable.
     * Built-in exercises (no owner) only by admins.
     */
    private boolean canEdit(Exercise exercise, UserDetailsImpl user) {

        if (exercise.getCreatedBy() == null) {
            return isAdmin(user);
        }

        return exercise.getCreatedBy().equals(user.getId());
    }

    /*
     * Another user's custom exercise is reported as "not found",
     * so its existence is not revealed.
     */
    private Exercise findVisible(Long id, UserDetailsImpl user) {

        return exerciseRepository
                .findVisibleById(id, user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Exercise not found with ID: " + id
                        )
                );
    }

    private Exercise findEditable(Long id, UserDetailsImpl user) {

        Exercise exercise = findVisible(id, user);

        if (!canEdit(exercise, user)) {
            throw new AccessDeniedException(
                    "Built-in exercises can only be changed by an administrator."
                            + " Create your own copy instead."
            );
        }

        return exercise;
    }

    private List<ExerciseResponseDTO> findAllVisible(
            UserDetailsImpl user,
            Predicate<Exercise> filter
    ) {

        return exerciseRepository
                .findAllVisible(user.getId())
                .stream()
                .filter(filter)
                .map(exercise -> mapToResponseDTO(exercise, user))
                .collect(Collectors.toList());
    }

    // =========================================================
    // TRACKING TYPE
    // =========================================================

    /*
     * REPS exercises store defaultReps and no duration.
     * TIME exercises store durationSeconds and no reps.
     */
    private void applyTracking(
            Exercise exercise,
            ExerciseRequestDTO requestDTO
    ) {

        ExerciseTrackingType trackingType =
                requestDTO.getTrackingType() == null
                        ? ExerciseTrackingType.REPS
                        : requestDTO.getTrackingType();

        exercise.setTrackingType(trackingType);

        if (trackingType == ExerciseTrackingType.TIME) {

            exercise.setDurationSeconds(
                    requestDTO.getDurationSeconds() == null
                            ? DEFAULT_DURATION_SECONDS
                            : requestDTO.getDurationSeconds()
            );

            exercise.setDefaultReps(null);

        } else {

            exercise.setDefaultReps(
                    requestDTO.getDefaultReps() == null
                            ? DEFAULT_REPS
                            : requestDTO.getDefaultReps()
            );

            exercise.setDurationSeconds(null);
        }
    }

    private ExerciseResponseDTO mapToResponseDTO(
            Exercise exercise,
            UserDetailsImpl user
    ) {

        ExerciseTrackingType trackingType =
                ExerciseTracking.resolve(exercise);

        boolean timeBased =
                trackingType == ExerciseTrackingType.TIME;

        ExerciseResponseDTO dto = ExerciseResponseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .category(exercise.getCategory())
                .workoutType(exercise.getWorkoutType())
                .equipment(exercise.getEquipment())
                .isCustom(exercise.getIsCustom())
                .trackingType(trackingType)
                .defaultReps(
                        timeBased
                                ? null
                                : exercise.getDefaultReps() == null
                                        ? DEFAULT_REPS
                                        : exercise.getDefaultReps()
                )
                .durationSeconds(
                        !timeBased
                                ? null
                                : exercise.getDurationSeconds() == null
                                        ? DEFAULT_DURATION_SECONDS
                                        : exercise.getDurationSeconds()
                )
                .restSeconds(
                        exercise.getRestSeconds() == null
                                ? DEFAULT_REST_SECONDS
                                : exercise.getRestSeconds()
                )
                .createdAt(exercise.getCreatedAt())
                .build();

        dto.setEditable(canEdit(exercise, user));

        return dto;
    }
}
