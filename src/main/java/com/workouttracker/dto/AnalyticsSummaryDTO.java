package com.workouttracker.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsSummaryDTO {

    private Long totalWorkouts;
    private Long totalSetsCompleted;
    private BigDecimal totalVolumeLifted;
    private Integer totalDurationMinutes;
    private Map<String, Long> workoutsByCategory; // e.g., {"Chest": 5, "Back": 3}
    private List<PersonalRecordDTO> topPersonalRecords;

    public AnalyticsSummaryDTO() {}

    public AnalyticsSummaryDTO(Long totalWorkouts, Long totalSetsCompleted, BigDecimal totalVolumeLifted, Integer totalDurationMinutes, Map<String, Long> workoutsByCategory, List<PersonalRecordDTO> topPersonalRecords) {
        this.totalWorkouts = totalWorkouts;
        this.totalSetsCompleted = totalSetsCompleted;
        this.totalVolumeLifted = totalVolumeLifted;
        this.totalDurationMinutes = totalDurationMinutes;
        this.workoutsByCategory = workoutsByCategory;
        this.topPersonalRecords = topPersonalRecords;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long totalWorkouts;
        private Long totalSetsCompleted;
        private BigDecimal totalVolumeLifted;
        private Integer totalDurationMinutes;
        private Map<String, Long> workoutsByCategory;
        private List<PersonalRecordDTO> topPersonalRecords;

        public Builder totalWorkouts(Long totalWorkouts) { this.totalWorkouts = totalWorkouts; return this; }
        public Builder totalSetsCompleted(Long totalSetsCompleted) { this.totalSetsCompleted = totalSetsCompleted; return this; }
        public Builder totalVolumeLifted(BigDecimal totalVolumeLifted) { this.totalVolumeLifted = totalVolumeLifted; return this; }
        public Builder totalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; return this; }
        public Builder workoutsByCategory(Map<String, Long> workoutsByCategory) { this.workoutsByCategory = workoutsByCategory; return this; }
        public Builder topPersonalRecords(List<PersonalRecordDTO> topPersonalRecords) { this.topPersonalRecords = topPersonalRecords; return this; }
        public AnalyticsSummaryDTO build() {
            return new AnalyticsSummaryDTO(totalWorkouts, totalSetsCompleted, totalVolumeLifted, totalDurationMinutes, workoutsByCategory, topPersonalRecords);
        }
    }

    public Long getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(Long totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public Long getTotalSetsCompleted() { return totalSetsCompleted; }
    public void setTotalSetsCompleted(Long totalSetsCompleted) { this.totalSetsCompleted = totalSetsCompleted; }

    public BigDecimal getTotalVolumeLifted() { return totalVolumeLifted; }
    public void setTotalVolumeLifted(BigDecimal totalVolumeLifted) { this.totalVolumeLifted = totalVolumeLifted; }

    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public Map<String, Long> getWorkoutsByCategory() { return workoutsByCategory; }
    public void setWorkoutsByCategory(Map<String, Long> workoutsByCategory) { this.workoutsByCategory = workoutsByCategory; }

    public List<PersonalRecordDTO> getTopPersonalRecords() { return topPersonalRecords; }
    public void setTopPersonalRecords(List<PersonalRecordDTO> topPersonalRecords) { this.topPersonalRecords = topPersonalRecords; }
}
