package com.workouttracker.controller;

import com.workouttracker.dto.AccountDTO;
import com.workouttracker.dto.ChangePasswordRequestDTO;
import com.workouttracker.dto.FeedbackRequestDTO;
import com.workouttracker.dto.JwtResponseDTO;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// The logged-in user's own account.
@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<AccountDTO> getMyAccount(
            @AuthenticationPrincipal UserDetailsImpl user) {

        return ResponseEntity.ok(accountService.getAccount(user.getUsername()));
    }

    /*
     * Fresh login token. The app calls this when opened, so people who
     * use it at least once a month stay logged in.
     */
    @PostMapping("/users/me/token")
    public ResponseEntity<JwtResponseDTO> refreshToken(
            @AuthenticationPrincipal UserDetailsImpl user) {

        return ResponseEntity.ok(accountService.issueToken(user.getUsername()));
    }

    @PutMapping("/users/me/password")
    public ResponseEntity<JwtResponseDTO> changePassword(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody ChangePasswordRequestDTO request) {

        return ResponseEntity.ok(accountService.changePassword(user.getUsername(), request));
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> sendFeedback(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody FeedbackRequestDTO request) {

        accountService.saveFeedback(user.getId(), request);

        return new ResponseEntity<>(
                Map.of("message", "Thank you! Your feedback was sent."),
                HttpStatus.CREATED);
    }
}
