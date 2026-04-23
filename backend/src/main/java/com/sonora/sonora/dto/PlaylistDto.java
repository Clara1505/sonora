package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.PlaylistType;

import java.time.LocalDateTime;
import java.util.List;

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
