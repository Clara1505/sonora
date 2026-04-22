package com.sonora.sonora.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Event published when a track is imported into storage. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackImportedEvent {
    /** Track identifier. */
    private Long trackId;
    /** Stored file path. */
    private String filePath;
    /** Track title. */
    private String title;
    /** Email of the uploader. */
    private String uploadedByEmail;
}
