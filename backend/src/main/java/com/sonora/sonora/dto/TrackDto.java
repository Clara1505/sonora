package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;

public record TrackDto(
        Long id,
        String title,
        Integer duration,
        Genre genre,
        Mood mood,
        String filePath,
        Long albumId,
        Long artistId,
        String createdByEmail
) {
}
