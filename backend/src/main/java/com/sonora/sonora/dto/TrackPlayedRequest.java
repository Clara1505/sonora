package com.sonora.sonora.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload to report listened duration for a track play.
 *
 * @param durationListened listened duration in seconds
 */
public record TrackPlayedRequest(
        @NotNull(message = "durationListened is required")
        @Min(value = 1, message = "durationListened must be >= 1")
        Integer durationListened
) {
}
