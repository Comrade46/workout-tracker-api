package com.workouttracker.controller;

import com.workouttracker.model.User;
import com.workouttracker.model.Workout;
import com.workouttracker.repository.UserRepository;
import com.workouttracker.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Fetch only workouts belonging to the logged-in user
    @GetMapping
    public List<Workout> getAllWorkouts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return workoutRepository.findByUserUsername(username);
    }

    // 2. Automatically assign the logged-in user when creating a workout
    @PostMapping
    public ResponseEntity<?> createWorkout(@RequestBody Workout workout) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        
        workout.setUser(user);
        Workout savedWorkout = workoutRepository.save(workout);
        return ResponseEntity.ok(savedWorkout);
    }

    // 3. Delete a workout only if it belongs to the logged-in user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkout(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Workout workout = workoutRepository.findById(id).orElse(null);

        if (workout == null) {
            return ResponseEntity.notFound().build();
        }

        // Security check: ensure user owns this workout
        if (!workout.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Unauthorized to delete this workout");
        }

        workoutRepository.deleteById(id);
        return ResponseEntity.ok().body("{\"message\": \"Workout deleted successfully\"}");
    }
}