package com.workouttracker.service;

import com.workouttracker.dto.UserLoginRequestDTO;
import com.workouttracker.dto.UserRegisterRequestDTO;
import com.workouttracker.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO registerUser(UserRegisterRequestDTO requestDTO);

    UserResponseDTO loginUser(UserLoginRequestDTO requestDTO);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);
}