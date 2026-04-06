package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import lombok.Data;

@Data
public class TrackUploadRequest {
    private String  title;
    private Integer duration;
    private Long    artistId;
    private Long    albumId;
    private Genre   genre;
    private Mood    mood;
}