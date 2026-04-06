package com.sonora.sonora.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlbumCreateUpdateDto(
        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Min(value = 1000, message = "releaseYear must be >= 1000")
        @Max(value = 3000, message = "releaseYear must be <= 3000")
        Integer releaseYear,

        @Size(max = 1024, message = "coverUrl must be at most 1024 characters")
        String coverUrl,

        @NotNull(message = "artistId is required")
        Long artistId
) {
}
