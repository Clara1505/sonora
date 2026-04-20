package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.model.enums.PlaylistType;

public record PlaylistCreateDto(
        String name,
        PlaylistType type,
        Mood mood,
        Genre genre
) {
}
