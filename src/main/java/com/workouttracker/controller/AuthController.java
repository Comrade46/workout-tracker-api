package com.workouttracker.controller;

import com.workouttracker.dto.JwtResponseDTO;
import com.workouttracker.dto.PasswordHelpRequestDTO;
import com.workouttracker.dto.UserLoginRequestDTO;
import com.workouttracker.dto.UserRegisterRequestDTO;
import com.workouttracker.dto.UserResponseDTO;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.AccountService;
import com.workouttracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          AccountService accountService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegisterRequestDTO requestDTO) {
        UserResponseDTO response = userService.registerUser(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(
            @Valid @RequestBody UserLoginRequestDTO loginRequest) {
        
        // 1. Authenticate with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(), 
                        loginRequest.getPassword()
                )
        );

        // 2. Set Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Token + account flags (admin, must change password)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(accountService.issueToken(userDetails.getUsername()));
    }

    /*
     * "Forgot password?" on the login page: records a request for the
     * admin. The answer is the same whether or not the account exists.
     */
    @PostMapping("/password-help")
    public ResponseEntity<Map<String, String>> requestPasswordHelp(
            @Valid @RequestBody PasswordHelpRequestDTO request) {

        accountService.requestPasswordHelp(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message",
                "Request sent. If this account exists, the app admin will send you a temporary password."));
    }
}