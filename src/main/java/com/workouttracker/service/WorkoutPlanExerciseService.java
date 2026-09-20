package com.workouttracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.workouttracker.model.Exercise;
import com.workouttracker.model.WorkoutPlan;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.repository.ExerciseRepository;
import com.workouttracker.repository.WorkoutPlanExerciseRepository;
import com.workouttracker.repository.WorkoutPlanRepository;

@Service
public class WorkoutPlanExerciseService {

    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutPlanExerciseService(
            WorkoutPlanExerciseRepository workoutPlanExerciseRepository,
            WorkoutPlanRepository workoutPlanRepository,
            ExerciseRepository exerciseRepository) {

        this.workoutPlanExerciseRepository = workoutPlanExerciseRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public WorkoutPlanExercise addExerciseToWorkoutPlan(
            Long workoutPlanId,
            Long exerciseId,
            Integer durationSeconds,
            Integer restSeconds,
            Integer exerciseOrder) {

        WorkoutPlan workoutPlan =
                workoutPlanRepository.findById(workoutPlanId)
                        .orElseThrow(() -> new RuntimeException(
                                "Workout plan not found with id: "
                                        + workoutPlanId));

        Exercise exercise =
                exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException(
                                "Exercise not found with id: "
                                        + exerciseId));

        WorkoutPlanExercise workoutPlanExercise =
                new WorkoutPlanExercise();

        workoutPlanExercise.setWorkoutPlan(workoutPlan);
        workoutPlanExercise.setExercise(exercise);
        workoutPlanExercise.setDurationSeconds(durationSeconds);
        workoutPlanExercise.setRestSeconds(restSeconds);
        workoutPlanExercise.setExerciseOrder(exerciseOrder);

        return workoutPlanExerciseRepository.save(
                workoutPlanExercise);
    }

    public List<WorkoutPlanExercise> getExercisesByWorkoutPlan(
            Long workoutPlanId) {

        return workoutPlanExerciseRepository
                .findByWorkoutPlanIdOrderByExerciseOrderAsc(
                        workoutPlanId);
    }

    public WorkoutPlanExercise getWorkoutPlanExerciseById(
            Long id) {

        return workoutPlanExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Workout plan exercise not found with id: "
                                + id));
    }

    public void deleteExerciseFromWorkoutPlan(Long id) {

        if (!workoutPlanExerciseRepository.existsById(id)) {

            throw new RuntimeException(
                    "Workout plan exercise not found with id: "
                            + id);
        }

        workoutPlanExerciseRepository.deleteById(id);
    }
}