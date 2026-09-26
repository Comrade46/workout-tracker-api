package com.workouttracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * "Forgot password" request from the login page. The admin sees open
 * requests and gives the user a temporary password.
 */
@Entity
@Table(name = "password_reset_requests")
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 300)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Null while the request is open
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public PasswordResetRequest() {}

    public PasswordResetRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
