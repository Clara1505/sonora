package com.sonora.sonora.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Event published when a track is played by a user. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackPlayedEvent {
    /** Played track identifier. */
    private Long trackId;
    /** User email that played the track. */
    private String userEmail;
    /** Listened duration in seconds. */
    private Integer durationListened;
}
