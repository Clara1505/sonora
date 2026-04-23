package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.model.enums.PlaylistType;

/**
 * Request payload for creating playlists.
 *
 * @param name playlist name
 * @param type playlist type
 * @param mood mood filter for smart playlists
 * @param genre genre filter for smart playlists
 */
public record PlaylistCreateDto(
        String name,
        PlaylistType type,
        Mood mood,
        Genre genre
) {
}
