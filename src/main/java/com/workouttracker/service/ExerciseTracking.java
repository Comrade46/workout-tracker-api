package com.workouttracker.service;

import com.workouttracker.model.Exercise;
import com.workouttracker.model.ExerciseTrackingType;

import java.util.Locale;
import java.util.regex.Pattern;

/*
 * Single source of truth for how an exercise is measured.
 *
 * The exercise itself decides TIME or REPS (Plank = TIME,
 * Push-Ups = REPS). Workout plans and the workout player must follow
 * it, so a plan can never show a timer for Russian Twists or a reps
 * form for Plank.
 */
public final class ExerciseTracking {

    public static final int DEFAULT_REPS = 12;
    public static final int DEFAULT_DURATION_SECONDS = 30;

    /*
     * Whole-word keywords used to classify exercises that were
     * created before tracking_type existed. Anything else is REPS.
     *
     * Whole words avoid false matches such as "crunches" -> "run"
     * or "walking lunges" -> "walk".
     */
    private static final Pattern TIME_KEYWORDS = Pattern.compile(
            "\\b(plank|hold|wall sit|dead hang|run|running|jog|jogging"
                    + "|sprint|walk|cycling|treadmill|elliptical|skipping"
                    + "|jump rope|stretch|high knees|butt kicks"
                    + "|mountain climbers?|jumping jacks?|battle ropes?"
                    + "|carry|hollow body|l-sit|shadow boxing)\\b"
    );

    private ExerciseTracking() {
    }

    public static ExerciseTrackingType resolve(Exercise exercise) {

        if (exercise == null) {
            return ExerciseTrackingType.REPS;
        }

        if (exercise.getTrackingType() != null) {
            return exercise.getTrackingType();
        }

        String name = exercise.getName() == null
                ? ""
                : exercise.getName().toLowerCase(Locale.ROOT);

        boolean timeBased =
                "cardio".equalsIgnoreCase(exercise.getCategory())
                        || TIME_KEYWORDS.matcher(name).find();

        return timeBased
                ? ExerciseTrackingType.TIME
                : ExerciseTrackingType.REPS;
    }

    // Default target: seconds for TIME exercises, reps for REPS exercises.
    public static int defaultTarget(Exercise exercise) {

        if (resolve(exercise) == ExerciseTrackingType.TIME) {
            return exercise.getDurationSeconds() == null
                    ? DEFAULT_DURATION_SECONDS
                    : exercise.getDurationSeconds();
        }

        return exercise.getDefaultReps() == null
                ? DEFAULT_REPS
                : exercise.getDefaultReps();
    }

    /*
     * Target for a plan row: keep the planned value only when it was
     * planned with the exercise's real type; otherwise (old rows, or a
     * row saved as REPS for Plank) fall back to the exercise default.
     */
    public static int planTarget(
            Exercise exercise,
            ExerciseTrackingType plannedType,
            Integer plannedValue) {

        boolean sameType = plannedType == resolve(exercise);

        if (sameType && plannedValue != null && plannedValue > 0) {
            return plannedValue;
        }

        return defaultTarget(exercise);
    }
}
