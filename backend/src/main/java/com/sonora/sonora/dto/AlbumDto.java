package com.sonora.sonora.dto;

public record AlbumDto(
        Long id,
        String title,
        Integer releaseYear,
        String coverUrl,
        Long artistId
) {
}
