package com.sonora.sonora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistCreateUpdateDto(
        @NotBlank(message = "name is required")
        @Size(max = 255, message = "name must be at most 255 characters")
        String name,

        @Size(max = 5000, message = "bio must be at most 5000 characters")
        String bio,

        @Size(max = 1024, message = "imageUrl must be at most 1024 characters")
        String imageUrl
) {
}
