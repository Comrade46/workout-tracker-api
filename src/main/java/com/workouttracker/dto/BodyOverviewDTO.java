package com.workouttracker.dto;

import java.util.List;

// Everything the app needs for the body / goals cards in one call.
public record BodyOverviewDTO(BodyProfileDTO profile, List<BodyWeightDTO> weights) {
}
