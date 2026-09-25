package com.workouttracker.controller;

import com.workouttracker.dto.JwtResponseDTO;
import com.workouttracker.dto.UserLoginRequestDTO;
import com.workouttracker.dto.UserRegisterRequestDTO;
import com.workouttracker.dto.UserResponseDTO;
import com.workouttracker.security.JwtUtils;
import com.workouttracker.security.UserDetailsImpl;
import com.workouttracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
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

        // 3. Generate Token
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

        // 4. Return Token to Client
        return ResponseEntity.ok(new JwtResponseDTO(
                jwt, 
                userDetails.getId(), 
                userDetails.getUsername(), 
                userDetails.getEmail()
        ));
    }
}