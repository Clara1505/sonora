package com.sonora.sonora.dto;

/**
 * Artist data returned by API endpoints.
 *
 * @param id artist identifier
 * @param name artist display name
 * @param bio artist biography
 * @param imageUrl artist image URL
 */
public record ArtistDto(
        Long id,
        String name,
        String bio,
        String imageUrl
) {
}
