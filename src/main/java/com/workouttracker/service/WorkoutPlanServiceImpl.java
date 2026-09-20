
package com.workouttracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.workouttracker.model.User;
import com.workouttracker.model.WorkoutPlan;
import com.workouttracker.repository.WorkoutPlanRepository;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;

    public WorkoutPlanServiceImpl(
            WorkoutPlanRepository workoutPlanRepository) {

        this.workoutPlanRepository = workoutPlanRepository;
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
                .orElseThrow(() -> new RuntimeException(
                        "Workout plan not found with id: " + id));
    }

    @Override
    public WorkoutPlan updateWorkoutPlan(
            Long id,
            WorkoutPlan updatedPlan) {

        WorkoutPlan existingPlan =
                workoutPlanRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Workout plan not found with id: " + id));

        existingPlan.setName(updatedPlan.getName());
        existingPlan.setDescription(updatedPlan.getDescription());
        existingPlan.setCategory(updatedPlan.getCategory());
        existingPlan.setDifficulty(updatedPlan.getDifficulty());

        return workoutPlanRepository.save(existingPlan);
    }

    @Override
    public void deleteWorkoutPlan(Long id) {

        if (!workoutPlanRepository.existsById(id)) {

            throw new RuntimeException(
                    "Workout plan not found with id: " + id);
        }

        workoutPlanRepository.deleteById(id);
    }
}

