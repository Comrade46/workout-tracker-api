package com.workouttracker.controller;

import com.workouttracker.dto.BodyOverviewDTO;
import com.workouttracker.dto.BodyProfileDTO;
import com.workouttracker.dto.BodyWeightDTO;
import com.workouttracker.dto.BodyWeightRequestDTO;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.BodyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// The logged-in user's body stats, goals and weight log.
@RestController
@RequestMapping("/api/users/me/body")
public class BodyController {

    private final BodyService bodyService;

    public BodyController(BodyService bodyService) {
        this.bodyService = bodyService;
    }

    @GetMapping
    public ResponseEntity<BodyOverviewDTO> getBody(
            @AuthenticationPrincipal UserDetailsImpl user) {

        return ResponseEntity.ok(bodyService.getOverview(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<BodyProfileDTO> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody BodyProfileDTO request) {

        return ResponseEntity.ok(bodyService.updateProfile(user.getId(), request));
    }

    // Log a weight; a second reading on the same day replaces the first.
    @PutMapping("/weights")
    public ResponseEntity<BodyWeightDTO> saveWeight(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody BodyWeightRequestDTO request) {

        return ResponseEntity.ok(bodyService.saveWeight(user.getId(), request));
    }

    @DeleteMapping("/weights/{entryId}")
    public ResponseEntity<Map<String, String>> deleteWeight(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long entryId) {

        bodyService.deleteWeight(user.getId(), entryId);
        return ResponseEntity.ok(Map.of("message", "Weight entry deleted."));
    }
}
