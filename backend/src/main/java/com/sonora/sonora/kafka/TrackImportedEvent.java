package com.sonora.sonora.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackImportedEvent {
    private Long   trackId;
    private String filePath;
    private String title;
    private String uploadedByEmail;
}