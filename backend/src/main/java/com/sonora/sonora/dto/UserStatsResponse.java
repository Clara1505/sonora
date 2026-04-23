package com.sonora.sonora.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Aggregated listening statistics for a user. */
@Data
@Builder
public class UserStatsResponse {
    /** User email for which statistics are returned. */
    private String userEmail;
    /** Total number of played tracks. */
    private long totalPlays;
    /** Total listening time in seconds across all plays. */
    private long totalListeningTime;
    /** Most played tracks for the user. */
    private List<TrackStatsResponse> topTracks;
}
