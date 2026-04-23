package com.sonora.sonora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating an artist.
 *
 * @param name artist name
 * @param bio artist biography
 * @param imageUrl artist image URL
 */
public record ArtistCreateUpdateDto(
        @NotBlank(message = "name is required")
        @Size(max = ArtistCreateUpdateDto.MAX_NAME_LENGTH,
                message = "name must be at most 255 characters")
        String name,

        @Size(max = ArtistCreateUpdateDto.MAX_BIO_LENGTH,
                message = "bio must be at most 5000 characters")
        String bio,

        @Size(max = ArtistCreateUpdateDto.MAX_IMAGE_URL_LENGTH,
                message = "imageUrl must be at most 1024 characters")
        String imageUrl
) {
    /** Maximum artist name length. */
    public static final int MAX_NAME_LENGTH = 255;

    /** Maximum biography length. */
    public static final int MAX_BIO_LENGTH = 5000;

    /** Maximum image URL length. */
    public static final int MAX_IMAGE_URL_LENGTH = 1024;
}
