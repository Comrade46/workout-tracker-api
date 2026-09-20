package com.workouttracker.service;

import com.workouttracker.dto.AnalyticsSummaryDTO;
import com.workouttracker.dto.PersonalRecordDTO;

import java.util.List;

public interface AnalyticsService {

    AnalyticsSummaryDTO getUserAnalyticsSummary(Long userId);

    List<PersonalRecordDTO> getUserPersonalRecords(Long userId);

    PersonalRecordDTO getExercisePR(Long userId, Long exerciseId);
}