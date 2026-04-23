package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;

/**
 * Track projection used in playlist responses.
 *
 * @param id track identifier
 * @param title track title
 * @param duration track duration in seconds
 * @param genre track genre
 * @param mood track mood
 * @param artist artist projection
 */
public record PlaylistTrackDto(
        Long id,
        String title,
        Integer duration,
        Genre genre,
        Mood mood,
        ArtistDto artist
) {
}
