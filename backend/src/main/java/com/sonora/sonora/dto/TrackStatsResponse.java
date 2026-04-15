package com.sonora.sonora.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackStatsResponse {
    private Long   trackId;
    private String trackTitle;
    private long   playCount;
    private Long   totalDurationListened; // en secondes
}