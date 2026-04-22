package com.sonora.sonora.dto;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating a track.
 *
 * @param title track title
 * @param duration track duration in seconds
 * @param genre track genre
 * @param mood track mood
 * @param filePath file path
 * @param albumId album identifier
 * @param artistId artist identifier
 */
public record TrackCreateUpdateDto(
        @NotBlank(message = "title is required")
        @Size(max = TrackCreateUpdateDto.MAX_TITLE_LENGTH,
                message = "title must be at most 255 characters")
        String title,

        @NotNull(message = "duration is required")
        @Min(value = 1, message = "duration must be >= 1")
        Integer duration,

        Genre genre,
        Mood mood,

        @Size(max = TrackCreateUpdateDto.MAX_FILE_PATH_LENGTH,
                message = "filePath must be at most 1024 characters")
        String filePath,

        Long albumId,

        @NotNull(message = "artistId is required")
        Long artistId
) {
        /** Maximum track title length. */
        public static final int MAX_TITLE_LENGTH = 255;

        /** Maximum file path length. */
        public static final int MAX_FILE_PATH_LENGTH = 1024;
}
