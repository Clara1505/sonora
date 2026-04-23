package com.sonora.sonora.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating an album.
 *
 * @param title album title
 * @param releaseYear album release year
 * @param coverUrl album cover URL
 * @param artistId associated artist identifier
 */
public record AlbumCreateUpdateDto(
        @NotBlank(message = "title is required")
        @Size(max = AlbumCreateUpdateDto.MAX_TITLE_LENGTH,
                message = "title must be at most 255 characters")
        String title,

        @Min(value = AlbumCreateUpdateDto.MIN_RELEASE_YEAR,
                message = "releaseYear must be >= 1000")
        @Max(value = AlbumCreateUpdateDto.MAX_RELEASE_YEAR,
                message = "releaseYear must be <= 3000")
        Integer releaseYear,

        @Size(max = AlbumCreateUpdateDto.MAX_COVER_URL_LENGTH,
                message = "coverUrl must be at most 1024 characters")
        String coverUrl,

        @NotNull(message = "artistId is required")
        Long artistId
) {
    /** Maximum album title length. */
    public static final int MAX_TITLE_LENGTH = 255;

    /** Lower bound for accepted release years. */
    public static final int MIN_RELEASE_YEAR = 1000;

    /** Upper bound for accepted release years. */
    public static final int MAX_RELEASE_YEAR = 3000;

    /** Maximum cover URL length. */
    public static final int MAX_COVER_URL_LENGTH = 1024;
}
