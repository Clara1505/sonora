package com.sonora.sonora.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackPlayedEvent {
    private Long   trackId;
    private String userEmail;
    private Integer durationListened; // en secondes
}