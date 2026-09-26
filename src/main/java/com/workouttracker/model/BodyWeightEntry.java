package com.workouttracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// One body-weight reading per user per day.
@Entity
@Table(
    name = "body_weight_entries",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_body_weight_user_date",
        columnNames = {"user_id", "entry_date"}
    )
)
public class BodyWeightEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "weight_kg", nullable = false, precision = 5, scale = 1)
    private BigDecimal weightKg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BodyWeightEntry() {}

    public BodyWeightEntry(Long userId, LocalDate entryDate, BigDecimal weightKg) {
        this.userId = userId;
        this.entryDate = entryDate;
        this.weightKg = weightKg;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getEntryDate() { return entryDate; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
}
