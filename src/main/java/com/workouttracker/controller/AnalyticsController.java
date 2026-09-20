package com.workouttracker.controller;

import com.workouttracker.dto.AnalyticsSummaryDTO;
import com.workouttracker.dto.PersonalRecordDTO;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    /**
     * Get overall analytics summary for the
     * currently authenticated user.
     *
     * GET /api/analytics/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary(
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                analyticsService.getUserAnalyticsSummary(userId)
        );
    }

    /**
     * Get all Personal Records for the
     * currently authenticated user.
     *
     * GET /api/analytics/prs
     */
    @GetMapping("/prs")
    public ResponseEntity<List<PersonalRecordDTO>> getAllPRs(
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                analyticsService.getUserPersonalRecords(userId)
        );
    }

    /**
     * Get Personal Record for a specific exercise
     * for the currently authenticated user.
     *
     * GET /api/analytics/prs/{exerciseId}
     */
    @GetMapping("/prs/{exerciseId}")
    public ResponseEntity<PersonalRecordDTO> getExercisePR(
            @PathVariable Long exerciseId,
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                analyticsService.getExercisePR(
                        userId,
                        exerciseId
                )
        );
    }

    /**
     * Gets the database ID of the currently authenticated user.
     *
     * JWT
     *   ↓
     * AuthTokenFilter
     *   ↓
     * UserDetailsImpl
     *   ↓
     * getId()
     */
    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof UserDetailsImpl)) {

            throw new IllegalStateException(
                    "Authenticated user information is not available"
            );
        }

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        return userDetails.getId();
    }
}