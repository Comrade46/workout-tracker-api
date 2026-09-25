package com.workouttracker.service;

import com.workouttracker.dto.UserLoginRequestDTO;
import com.workouttracker.dto.UserRegisterRequestDTO;
import com.workouttracker.dto.UserResponseDTO;
import com.workouttracker.exception.DuplicateResourceException;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRegisterRequestDTO requestDTO) {
        // Normalise first so the duplicate checks match what is stored
        String username = requestDTO.getUsername().trim();
        String email = requestDTO.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username is already taken: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered: " + email);
        }

        // Hash the raw password with BCrypt before persisting
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO loginUser(UserLoginRequestDTO requestDTO) {
        String identifier = requestDTO.getUsernameOrEmail().trim();

        // Search by username or email
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username/email or password"));

        // Verify raw password matches stored BCrypt hash
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username/email or password");
        }

        return mapToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToResponseDTO(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
