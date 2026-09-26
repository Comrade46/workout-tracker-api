package com.workouttracker.controller;

import com.workouttracker.dto.AccountDTO;
import com.workouttracker.dto.FeedbackDTO;
import com.workouttracker.dto.PasswordResetRequestDTO;
import com.workouttracker.dto.TemporaryPasswordDTO;
import com.workouttracker.security.AdminAccess;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Admin tools. Only usernames in ADMIN_USERNAMES get past requireAdmin.
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AccountService accountService;
    private final AdminAccess adminAccess;

    public AdminController(AccountService accountService, AdminAccess adminAccess) {
        this.accountService = accountService;
        this.adminAccess = adminAccess;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AccountDTO>> listUsers(
            @AuthenticationPrincipal UserDetailsImpl admin) {

        adminAccess.requireAdmin(admin);
        return ResponseEntity.ok(accountService.listUsers());
    }

    @PostMapping("/users/{userId}/temporary-password")
    public ResponseEntity<TemporaryPasswordDTO> createTemporaryPassword(
            @AuthenticationPrincipal UserDetailsImpl admin,
            @PathVariable Long userId) {

        adminAccess.requireAdmin(admin);
        return ResponseEntity.ok(accountService.createTemporaryPassword(userId));
    }

    @GetMapping("/password-requests")
    public ResponseEntity<List<PasswordResetRequestDTO>> openPasswordRequests(
            @AuthenticationPrincipal UserDetailsImpl admin) {

        adminAccess.requireAdmin(admin);
        return ResponseEntity.ok(accountService.openPasswordRequests());
    }

    @PostMapping("/password-requests/{requestId}/dismiss")
    public ResponseEntity<Map<String, String>> dismissPasswordRequest(
            @AuthenticationPrincipal UserDetailsImpl admin,
            @PathVariable Long requestId) {

        adminAccess.requireAdmin(admin);
        accountService.dismissPasswordRequest(requestId);
        return ResponseEntity.ok(Map.of("message", "Request closed."));
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackDTO>> listFeedback(
            @AuthenticationPrincipal UserDetailsImpl admin) {

        adminAccess.requireAdmin(admin);
        return ResponseEntity.ok(accountService.listFeedback());
    }

    @PutMapping("/feedback/{feedbackId}")
    public ResponseEntity<Map<String, String>> setFeedbackResolved(
            @AuthenticationPrincipal UserDetailsImpl admin,
            @PathVariable Long feedbackId,
            @RequestBody Map<String, Boolean> body) {

        adminAccess.requireAdmin(admin);
        accountService.setFeedbackResolved(feedbackId, Boolean.TRUE.equals(body.get("resolved")));
        return ResponseEntity.ok(Map.of("message", "Saved."));
    }
}
