package com.workouttracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonBackReference
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false)
    private Integer reps;

    private Integer rpe; // Rate of Perceived Exertion (1 to 10 scale)

    public WorkoutSet() {}

    public WorkoutSet(Long id, WorkoutSession session, Exercise exercise, Integer setNumber, BigDecimal weight, Integer reps, Integer rpe) {
        this.id = id;
        this.session = session;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkoutSession session;
        private Exercise exercise;
        private Integer setNumber;
        private BigDecimal weight;
        private Integer reps;
        private Integer rpe;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder session(WorkoutSession session) { this.session = session; return this; }
        public Builder exercise(Exercise exercise) { this.exercise = exercise; return this; }
        public Builder setNumber(Integer setNumber) { this.setNumber = setNumber; return this; }
        public Builder weight(BigDecimal weight) { this.weight = weight; return this; }
        public Builder reps(Integer reps) { this.reps = reps; return this; }
        public Builder rpe(Integer rpe) { this.rpe = rpe; return this; }
        public WorkoutSet build() {
            return new WorkoutSet(id, session, exercise, setNumber, weight, reps, rpe);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutSession getSession() { return session; }
    public void setSession(WorkoutSession session) { this.session = session; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Integer getRpe() { return rpe; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }
}