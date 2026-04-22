package com.sonora.sonora.dto;

/**
 * Album data returned by API endpoints.
 *
 * @param id album identifier
 * @param title album title
 * @param releaseYear release year
 * @param coverUrl cover URL
 * @param artistId artist identifier
 */
public record AlbumDto(
        Long id,
        String title,
        Integer releaseYear,
        String coverUrl,
        Long artistId
) {
}
