package com.workouttracker.repository;

import com.workouttracker.model.BodyWeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BodyWeightEntryRepository extends JpaRepository<BodyWeightEntry, Long> {

    List<BodyWeightEntry> findByUserIdOrderByEntryDateAsc(Long userId);

    Optional<BodyWeightEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

    Optional<BodyWeightEntry> findByIdAndUserId(Long id, Long userId);
}
