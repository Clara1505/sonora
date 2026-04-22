package com.sonora.sonora.dto;

import com.sonora.sonora.model.entity.Favorite;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** Favorite response payload returned by favorite endpoints. */
@Data
@Builder
public class FavoriteResponse {
    /** Favorite identifier. */
    private Long id;
    /** Referenced track identifier. */
    private Long trackId;
    /** Referenced track title. */
    private String trackTitle;
    /** Referenced track artist name. */
    private String artistName;
    /** Referenced track genre. */
    private Genre genre;
    /** Referenced track mood. */
    private Mood mood;
    /** Favorite creation timestamp. */
    private LocalDateTime addedAt;

    /**
     * Maps a favorite entity to an API response.
     *
     * @param favorite favorite entity to map
     * @return mapped response
     */
    public static FavoriteResponse from(final Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .trackId(favorite.getTrack().getId())
                .trackTitle(favorite.getTrack().getTitle())
                .artistName(favorite.getTrack().getArtist() != null
                        ? favorite.getTrack().getArtist().getName() : null)
                .genre(favorite.getTrack().getGenre())
                .mood(favorite.getTrack().getMood())
                .addedAt(favorite.getCreatedAt())
                .build();
    }
}
