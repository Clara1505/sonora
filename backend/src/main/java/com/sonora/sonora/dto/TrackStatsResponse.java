package com.sonora.sonora.dto;

import lombok.Builder;
import lombok.Data;

/** Aggregated statistics for a track. */
@Data
@Builder
public class TrackStatsResponse {
    /** Track identifier. */
    private Long trackId;
    /** Track title. */
    private String trackTitle;
    /** Number of plays for the track. */
    private long playCount;
    /** Total listened duration in seconds. */
    private Long totalDurationListened;
}
