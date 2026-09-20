package com.workouttracker.service;

import com.workouttracker.dto.ExerciseRequestDTO;
import com.workouttracker.dto.ExerciseResponseDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.Exercise;
import com.workouttracker.model.WorkoutType;
import com.workouttracker.repository.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    @Transactional
    public ExerciseResponseDTO createExercise(ExerciseRequestDTO requestDTO) {
        Exercise exercise = Exercise.builder()
                .name(requestDTO.getName().trim())
                .category(requestDTO.getCategory().trim())
                .workoutType(requestDTO.getWorkoutType())
                .equipment(requestDTO.getEquipment().trim())
                .durationSeconds(requestDTO.getDurationSeconds())
                .restSeconds(requestDTO.getRestSeconds())
                .isCustom(true)
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);
        return mapToResponseDTO(savedExercise);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponseDTO getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with ID: " + id));
        return mapToResponseDTO(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getExercisesByWorkoutType(WorkoutType workoutType) {
        return exerciseRepository.findByWorkoutType(workoutType)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> getExercisesByCategory(String category) {
        return exerciseRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseResponseDTO> searchExercisesByName(String keyword) {
        return exerciseRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExerciseResponseDTO updateExercise(Long id, ExerciseRequestDTO requestDTO) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with ID: " + id));

        exercise.setName(requestDTO.getName().trim());
        exercise.setCategory(requestDTO.getCategory().trim());
        exercise.setWorkoutType(requestDTO.getWorkoutType());
        exercise.setEquipment(requestDTO.getEquipment().trim());
        exercise.setDurationSeconds(requestDTO.getDurationSeconds());
        exercise.setRestSeconds(requestDTO.getRestSeconds());

        Exercise updatedExercise = exerciseRepository.save(exercise);
        return mapToResponseDTO(updatedExercise);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with ID: " + id));
        exerciseRepository.delete(exercise);
    }

    private ExerciseResponseDTO mapToResponseDTO(Exercise exercise) {
        return ExerciseResponseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .category(exercise.getCategory())
                .workoutType(exercise.getWorkoutType())
                .equipment(exercise.getEquipment())
                .isCustom(exercise.getIsCustom())
                .durationSeconds(exercise.getDurationSeconds() == null ? 30 : exercise.getDurationSeconds())
                .restSeconds(exercise.getRestSeconds() == null ? 15 : exercise.getRestSeconds())
                .createdAt(exercise.getCreatedAt())
                .build();
    }
}
