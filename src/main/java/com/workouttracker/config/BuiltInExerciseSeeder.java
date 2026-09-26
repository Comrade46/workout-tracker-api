package com.workouttracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/*
 * Adds the built-in exercises used by the ready-made programs
 * (30-day challenge, muscle-building splits, body-part workouts)
 * when they are missing. Existing exercises are never changed.
 *
 * Several app instances can start at the same time against the same
 * database, so the whole check-and-insert runs under a MySQL named lock
 * (GET_LOCK) on one connection, with each insert committed immediately.
 */
@Component
public class BuiltInExerciseSeeder implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(BuiltInExerciseSeeder.class);

    private static final String LOCK_NAME = "workout_tracker_seed_exercises";

    private record Seed(
            String name,
            String category,
            String workoutType,
            String equipment,
            String trackingType,
            Integer defaultReps,
            Integer durationSeconds,
            int restSeconds) {
    }

    private static Seed reps(String name, String category, String workoutType,
                             String equipment, int reps, int rest) {
        return new Seed(name, category, workoutType, equipment, "REPS", reps, null, rest);
    }

    private static Seed time(String name, String category, String workoutType,
                             String equipment, int seconds, int rest) {
        return new Seed(name, category, workoutType, equipment, "TIME", null, seconds, rest);
    }

    private static final List<Seed> SEEDS = List.of(
            // Chest
            reps("Flat Dumbbell Press", "Chest", "GYM", "Dumbbells", 10, 90),
            reps("Dumbbell Flyes", "Chest", "GYM", "Dumbbells", 12, 60),
            reps("Incline Push-Ups", "Chest", "HOME", "Bodyweight", 10, 45),

            // Shoulders
            reps("Dumbbell Shoulder Press", "Shoulders", "GYM", "Dumbbells", 10, 90),
            reps("Arnold Press", "Shoulders", "GYM", "Dumbbells", 10, 75),
            reps("Front Dumbbell Raise", "Shoulders", "GYM", "Dumbbells", 12, 60),
            reps("Rear Delt Flyes", "Shoulders", "GYM", "Dumbbells", 15, 60),
            reps("Cable Lateral Raise", "Shoulders", "GYM", "Cable", 15, 45),
            reps("Face Pulls", "Shoulders", "GYM", "Cable", 15, 60),

            // Back & traps
            reps("Pull-Ups", "Back", "BOTH", "Pull-up Bar", 8, 120),
            reps("Chin-Ups", "Back", "BOTH", "Pull-up Bar", 8, 120),
            reps("Bent-Over Barbell Row", "Back", "GYM", "Barbell", 10, 90),
            reps("One-Arm Dumbbell Row", "Back", "GYM", "Dumbbell", 12, 60),
            reps("Straight-Arm Pulldown", "Back", "GYM", "Cable", 12, 60),
            reps("Single-Arm Cable Lat Pulldown", "Back", "GYM", "Cable", 12, 60),
            reps("Chest-Supported Row", "Back", "GYM", "Dumbbells", 10, 75),
            reps("Dumbbell Shrugs", "Back", "GYM", "Dumbbells", 15, 60),

            // Arms
            reps("Dumbbell Bicep Curl", "Arms", "GYM", "Dumbbells", 12, 60),
            reps("Hammer Curls", "Arms", "GYM", "Dumbbells", 12, 60),
            reps("Preacher Curl", "Arms", "GYM", "EZ Bar", 12, 60),
            reps("Skull Crushers", "Arms", "GYM", "EZ Bar", 10, 75),
            reps("Overhead Dumbbell Extension", "Arms", "GYM", "Dumbbell", 12, 60),
            reps("Close-Grip Bench Press", "Arms", "GYM", "Barbell", 8, 90),

            // Legs
            reps("Goblet Squat", "Legs", "BOTH", "Dumbbell", 12, 75),
            reps("Dumbbell Lunges", "Legs", "GYM", "Dumbbells", 10, 75),
            reps("Lying Leg Curl", "Legs", "GYM", "Machine", 12, 60),
            reps("Leg Extension", "Legs", "GYM", "Machine", 15, 60),
            reps("Standing Calf Raise", "Legs", "GYM", "Machine", 15, 45),
            reps("Jump Squats", "Legs", "HOME", "Bodyweight", 12, 45),
            reps("Glute Bridge", "Legs", "HOME", "Bodyweight", 15, 30),
            time("Wall Sit", "Legs", "HOME", "Bodyweight", 30, 30),

            // Core
            reps("Crunches", "Core", "HOME", "Bodyweight", 15, 30),
            reps("Reverse Crunches", "Core", "HOME", "Bodyweight", 15, 30),
            reps("Dead Bug", "Core", "HOME", "Bodyweight", 12, 30),
            reps("Hanging Leg Raises", "Core", "GYM", "Pull-up Bar", 12, 60),
            reps("Cable Crunch", "Core", "GYM", "Cable", 15, 45),
            time("Side Plank", "Core", "HOME", "Bodyweight", 30, 30),
            time("Flutter Kicks", "Core", "HOME", "Bodyweight", 30, 30)
    );

    private final DataSource dataSource;

    public BuiltInExerciseSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);

            if (!acquireLock(connection)) {
                log.warn("Exercise seeding skipped: could not get the seed lock");
                return;
            }

            try {
                int added = seedMissing(connection);

                if (added > 0) {
                    log.info("Added {} built-in exercises for programs", added);
                }
            } finally {
                releaseLock(connection);
            }

        } catch (Exception ex) {
            // Never stop the application because of seeding.
            log.warn("Exercise seeding failed: {}", ex.getMessage());
        }
    }

    private int seedMissing(Connection connection) throws Exception {

        Set<String> existing = new HashSet<>();

        try (PreparedStatement select = connection.prepareStatement(
                "SELECT LOWER(name) FROM exercises WHERE created_by IS NULL");
             ResultSet rs = select.executeQuery()) {

            while (rs.next()) {
                existing.add(rs.getString(1));
            }
        }

        int added = 0;

        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO exercises (name, category, workout_type, equipment, is_custom,"
                        + " duration_seconds, rest_seconds, created_at, tracking_type,"
                        + " default_reps, created_by)"
                        + " VALUES (?, ?, ?, ?, false, ?, ?, ?, ?, ?, NULL)")) {

            for (Seed seed : SEEDS) {

                if (existing.contains(seed.name().toLowerCase(Locale.ROOT))) {
                    continue;
                }

                insert.setString(1, seed.name());
                insert.setString(2, seed.category());
                insert.setString(3, seed.workoutType());
                insert.setString(4, seed.equipment());
                insert.setObject(5, seed.durationSeconds());
                insert.setInt(6, seed.restSeconds());
                insert.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                insert.setString(8, seed.trackingType());
                insert.setObject(9, seed.defaultReps());

                insert.executeUpdate();
                added++;
            }
        }

        return added;
    }

    private boolean acquireLock(Connection connection) throws Exception {
        try (PreparedStatement lock = connection.prepareStatement("SELECT GET_LOCK(?, 30)")) {
            lock.setString(1, LOCK_NAME);

            try (ResultSet rs = lock.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private void releaseLock(Connection connection) {
        try (PreparedStatement release = connection.prepareStatement("SELECT RELEASE_LOCK(?)")) {
            release.setString(1, LOCK_NAME);
            release.executeQuery().close();
        } catch (Exception ex) {
            log.warn("Could not release the seed lock: {}", ex.getMessage());
        }
    }
}
