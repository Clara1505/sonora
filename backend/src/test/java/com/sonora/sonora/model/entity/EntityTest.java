package com.sonora.sonora.model.entity;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.model.enums.PlaylistType;
import com.sonora.sonora.model.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void user_shouldBuildCorrectly() {
        User user = User.builder()
                .email("test@sonora.com")
                .passwordHash("hashed")
                .role(Role.USER)
                .build();

        assertThat(user.getEmail()).isEqualTo("test@sonora.com");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getPlaylists()).isEmpty();
        assertThat(user.getFavorites()).isEmpty();
    }

    @Test
    void artist_shouldBuildCorrectly() {
        Artist artist = Artist.builder()
                .name("Daft Punk")
                .bio("French electronic duo")
                .build();

        assertThat(artist.getName()).isEqualTo("Daft Punk");
        assertThat(artist.getAlbums()).isEmpty();
        assertThat(artist.getTracks()).isEmpty();
    }

    @Test
    void album_shouldBuildWithArtist() {
        Artist artist = Artist.builder().name("Daft Punk").build();
        Album album = Album.builder()
                .title("Random Access Memories")
                .releaseYear(2013)
                .artist(artist)
                .build();

        assertThat(album.getTitle()).isEqualTo("Random Access Memories");
        assertThat(album.getReleaseYear()).isEqualTo(2013);
        assertThat(album.getArtist()).isEqualTo(artist);
        assertThat(album.getTracks()).isEmpty();
    }

    @Test
    void track_shouldBuildWithGenreAndMood() {
        Artist artist = Artist.builder().name("Daft Punk").build();
        Track track = Track.builder()
                .title("Get Lucky")
                .duration(248)
                .genre(Genre.POP)
                .mood(Mood.HAPPY)
                .artist(artist)
                .build();

        assertThat(track.getTitle()).isEqualTo("Get Lucky");
        assertThat(track.getDuration()).isEqualTo(248);
        assertThat(track.getGenre()).isEqualTo(Genre.POP);
        assertThat(track.getMood()).isEqualTo(Mood.HAPPY);
        assertThat(track.getPlaylists()).isEmpty();
    }

    @Test
    void playlist_shouldBuildWithTypeAndUser() {
        User user = User.builder()
                .email("test@sonora.com")
                .passwordHash("hashed")
                .role(Role.USER)
                .build();

        Playlist playlist = Playlist.builder()
                .name("Ma playlist")
                .type(PlaylistType.MANUAL)
                .user(user)
                .build();

        assertThat(playlist.getName()).isEqualTo("Ma playlist");
        assertThat(playlist.getType()).isEqualTo(PlaylistType.MANUAL);
        assertThat(playlist.getCreatedAt()).isNotNull();
        assertThat(playlist.getTracks()).isEmpty();
    }

    @Test
    void playHistory_shouldBuildCorrectly() {
        User user = User.builder().email("a@b.com").passwordHash("x").role(Role.USER).build();
        Artist artist = Artist.builder().name("Test").build();
        Track track = Track.builder().title("Song").duration(180).artist(artist).build();

        PlayHistory history = PlayHistory.builder()
                .user(user)
                .track(track)
                .durationListened(120)
                .build();

        assertThat(history.getDurationListened()).isEqualTo(120);
        assertThat(history.getPlayedAt()).isNotNull();
        assertThat(history.getPlayedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void favorite_shouldBuildCorrectly() {
        User user = User.builder().email("a@b.com").passwordHash("x").role(Role.USER).build();
        Artist artist = Artist.builder().name("Test").build();
        Track track = Track.builder().title("Song").duration(180).artist(artist).build();

        Favorite favorite = Favorite.builder()
                .user(user)
                .track(track)
                .build();

        assertThat(favorite.getUser()).isEqualTo(user);
        assertThat(favorite.getTrack()).isEqualTo(track);
        assertThat(favorite.getCreatedAt()).isNotNull();
    }
}