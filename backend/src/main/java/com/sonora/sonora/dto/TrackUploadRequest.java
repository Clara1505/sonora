package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import lombok.Data;

/** Request payload used for track upload metadata. */
@Data
public class TrackUploadRequest {
    /** Track title. */
    private String title;
    /** Track duration in seconds. */
    private Integer duration;
    /** Associated artist identifier. */
    private Long artistId;
    /** Associated album identifier. */
    private Long albumId;
    /** Track genre. */
    private Genre genre;
    /** Track mood. */
    private Mood mood;
}
