package com.sonora.sonora.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserStatsResponse {
    private String             userEmail;
    private long               totalPlays;
    private List<TrackStatsResponse> topTracks;
}
