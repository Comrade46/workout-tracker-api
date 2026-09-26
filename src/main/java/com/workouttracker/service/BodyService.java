package com.workouttracker.service;

import com.workouttracker.dto.BodyOverviewDTO;
import com.workouttracker.dto.BodyProfileDTO;
import com.workouttracker.dto.BodyWeightDTO;
import com.workouttracker.dto.BodyWeightRequestDTO;
import com.workouttracker.exception.ResourceNotFoundException;
import com.workouttracker.model.BodyWeightEntry;
import com.workouttracker.model.User;
import com.workouttracker.repository.BodyWeightEntryRepository;
import com.workouttracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/*
 * Body & goals: height, weekly workout goal, main goal, target weight,
 * and the body-weight log (one reading per day).
 */
@Service
public class BodyService {

    private final UserRepository userRepository;
    private final BodyWeightEntryRepository weightRepository;

    public BodyService(UserRepository userRepository, BodyWeightEntryRepository weightRepository) {
        this.userRepository = userRepository;
        this.weightRepository = weightRepository;
    }

    @Transactional(readOnly = true)
    public BodyOverviewDTO getOverview(Long userId) {
        User user = findUser(userId);

        List<BodyWeightDTO> weights = weightRepository.findByUserIdOrderByEntryDateAsc(userId)
                .stream()
                .map(BodyService::toDTO)
                .toList();

        return new BodyOverviewDTO(toProfile(user), weights);
    }

    @Transactional
    public BodyProfileDTO updateProfile(Long userId, BodyProfileDTO request) {
        User user = findUser(userId);

        user.setHeightCm(request.heightCm());
        user.setWeeklyGoal(request.weeklyGoal());
        user.setFitnessGoal(request.fitnessGoal());
        user.setTargetWeightKg(oneDecimal(request.targetWeightKg()));

        return toProfile(userRepository.save(user));
    }

    // Adds a reading, or replaces the one already logged for that day.
    @Transactional
    public BodyWeightDTO saveWeight(Long userId, BodyWeightRequestDTO request) {
        // One day of slack: the phone's date can be ahead of the server's.
        if (request.date().isAfter(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("The date cannot be in the future.");
        }

        if (request.date().isBefore(LocalDate.of(1990, 1, 1))) {
            throw new IllegalArgumentException("Please choose a more recent date.");
        }

        BigDecimal weight = oneDecimal(request.weightKg());

        BodyWeightEntry entry = weightRepository
                .findByUserIdAndEntryDate(userId, request.date())
                .orElse(null);

        if (entry == null) {
            entry = new BodyWeightEntry(userId, request.date(), weight);
        } else {
            entry.setWeightKg(weight);
        }

        return toDTO(weightRepository.save(entry));
    }

    @Transactional
    public void deleteWeight(Long userId, Long entryId) {
        BodyWeightEntry entry = weightRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Weight entry not found with ID: " + entryId));

        weightRepository.delete(entry);
    }

    // ---------------------------------------------------------

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private static BodyProfileDTO toProfile(User user) {
        return new BodyProfileDTO(
                user.getHeightCm(),
                user.getWeeklyGoal(),
                user.getFitnessGoal(),
                user.getTargetWeightKg());
    }

    private static BodyWeightDTO toDTO(BodyWeightEntry entry) {
        return new BodyWeightDTO(entry.getId(), entry.getEntryDate(), entry.getWeightKg());
    }

    private static BigDecimal oneDecimal(BigDecimal value) {
        return value == null ? null : value.setScale(1, RoundingMode.HALF_UP);
    }
}
