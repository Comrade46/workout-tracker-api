package com.workouttracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.User;
import com.workouttracker.model.WorkoutPlan;
import com.workouttracker.model.WorkoutPlanExercise;
import com.workouttracker.repository.WorkoutPlanExerciseRepository;
import com.workouttracker.repository.WorkoutPlanRepository;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanExerciseRepository workoutPlanExerciseRepository;

    public WorkoutPlanServiceImpl(
            WorkoutPlanRepository workoutPlanRepository,
            WorkoutPlanExerciseRepository workoutPlanExerciseRepository) {

        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutPlanExerciseRepository =
                workoutPlanExerciseRepository;
    }

    @Override
    public WorkoutPlan createWorkoutPlan(
            WorkoutPlan workoutPlan,
            User user) {

        workoutPlan.setUser(user);
        workoutPlan.setCreatedAt(LocalDateTime.now());

        return workoutPlanRepository.save(workoutPlan);
    }

    @Override
    public List<WorkoutPlan> getUserWorkoutPlans(
            String username) {

        return workoutPlanRepository
                .findByUserUsername(username);
    }

    @Override
    public WorkoutPlan getWorkoutPlanById(Long id) {

        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workout plan not found with id: " + id));
    }

    @Override
    @Transactional
    public WorkoutPlan updateWorkoutPlan(
            Long id,
            WorkoutPlan updatedPlan) {

        WorkoutPlan existingPlan =
                workoutPlanRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Workout plan not found with id: "
                                        + id));

        existingPlan.setName(updatedPlan.getName());
        existingPlan.setDescription(
                updatedPlan.getDescription());
        existingPlan.setCategory(
                updatedPlan.getCategory());
        existingPlan.setDifficulty(
                updatedPlan.getDifficulty());

        return workoutPlanRepository.save(existingPlan);
    }

    @Override
    @Transactional
    public void deleteWorkoutPlan(Long id) {

        WorkoutPlan existingPlan =
                workoutPlanRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Workout plan not found with id: "
                                        + id));

        /*
         * Delete exercises attached to the plan first.
         * This prevents foreign-key constraint errors.
         */
        List<WorkoutPlanExercise> planExercises =
                workoutPlanExerciseRepository
                        .findByWorkoutPlanIdOrderByExerciseOrderAsc(
                                id);

        if (planExercises != null &&
                !planExercises.isEmpty()) {

            workoutPlanExerciseRepository.deleteAll(
                    planExercises);
        }

        /*
         * Now the workout plan itself can be deleted safely.
         */
        workoutPlanRepository.delete(existingPlan);
    }
}