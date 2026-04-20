package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;

public record PlaylistTrackDto(
        Long id,
        String title,
        Integer duration,
        Genre genre,
        Mood mood,
        ArtistDto artist
) {
}
