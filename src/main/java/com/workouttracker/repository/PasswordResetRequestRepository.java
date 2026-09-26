package com.workouttracker.repository;

import com.workouttracker.model.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {

    List<PasswordResetRequest> findByResolvedAtIsNullOrderByCreatedAtDesc();

    Optional<PasswordResetRequest> findFirstByUserIdAndResolvedAtIsNull(Long userId);

    List<PasswordResetRequest> findByUserIdAndResolvedAtIsNull(Long userId);
}
