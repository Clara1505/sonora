package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;

/**
 * Track data returned by API endpoints.
 *
 * @param id track identifier
 * @param title track title
 * @param duration track duration in seconds
 * @param genre track genre
 * @param mood track mood
 * @param filePath stored file path
 * @param albumId album identifier
 * @param artistId artist identifier
 * @param createdByEmail email of the user who created the track, if any
 */
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
