package com.workouttracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Message sent from the app's "Send feedback" form.
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(length = 200)
    private String page;

    @Column(length = 300)
    private String device;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved")
    private Boolean resolved;

    public Feedback() {}

    public Feedback(Long userId, String message, String appVersion, String page, String device) {
        this.userId = userId;
        this.message = message;
        this.appVersion = appVersion;
        this.page = page;
        this.device = device;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getAppVersion() { return appVersion; }
    public String getPage() { return page; }
    public String getDevice() { return device; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isResolved() { return Boolean.TRUE.equals(resolved); }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
