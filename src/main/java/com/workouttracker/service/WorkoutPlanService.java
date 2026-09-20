package com.workouttracker.service;

import java.util.List;

import com.workouttracker.model.User;
import com.workouttracker.model.WorkoutPlan;

public interface WorkoutPlanService {

    WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan, User user);

    List<WorkoutPlan> getUserWorkoutPlans(String username);

    WorkoutPlan getWorkoutPlanById(Long id);

    WorkoutPlan updateWorkoutPlan(Long id, WorkoutPlan updatedPlan);

    void deleteWorkoutPlan(Long id);
}