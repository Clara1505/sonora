package com.sonora.sonora.dto;

import com.sonora.sonora.model.entity.Favorite;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {
    private Long          id;
    private Long          trackId;
    private String        trackTitle;
    private String        artistName;
    private Genre         genre;
    private Mood          mood;
    private LocalDateTime addedAt;

    public static FavoriteResponse from(Favorite favorite) {
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