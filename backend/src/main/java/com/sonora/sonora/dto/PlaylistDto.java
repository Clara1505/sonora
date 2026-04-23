package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.PlaylistType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Playlist data returned by API endpoints.
 *
 * @param id playlist identifier
 * @param name playlist name
 * @param type playlist type
 * @param imageUrl playlist image URL
 * @param createdAt creation timestamp
 * @param tracks tracks in playlist
 */
public record PlaylistDto(
        Long id,
        String name,
        PlaylistType type,
        String imageUrl,
        LocalDateTime createdAt,
        String userEmail,
        List<PlaylistTrackDto> tracks
) {
}
