package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Album;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.repository.AlbumRepository;
import com.sonora.sonora.repository.ArtistRepository;
import com.sonora.sonora.repository.FavoriteRepository;
import com.sonora.sonora.repository.PlaylistRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrackService trackService;

    @Test
    void create_shouldSaveTrackWithoutAlbum() {
        Artist artist = artist(7L);
        TrackCreateUpdateDto dto = new TrackCreateUpdateDto(
                "One More Time",
                320,
                Genre.ELECTRONIC,
                Mood.HYPE,
                "/tracks/one-more-time.mp3",
                null,
                7L
        );

        when(artistRepository.findById(7L)).thenReturn(Optional.of(artist));
        when(trackRepository.save(any())).thenAnswer(invocation -> {
            Track track = invocation.getArgument(0);
            return Track.builder()
                    .id(11L)
                    .title(track.getTitle())
                    .duration(track.getDuration())
                    .genre(track.getGenre())
                    .mood(track.getMood())
                    .filePath(track.getFilePath())
                    .album(track.getAlbum())
                    .artist(track.getArtist())
                    .build();
        });

        TrackDto result = trackService.create(dto, "creator@test.com");

        assertThat(result).isEqualTo(new TrackDto(
                11L,
                "One More Time",
                320,
                Genre.ELECTRONIC,
                Mood.HYPE,
                "/tracks/one-more-time.mp3",
                null,
                7L,
                null
        ));
        verify(albumRepository, never()).findById(any());
    }

    @Test
    void create_shouldSaveTrackWithAlbum() {
        Artist artist = artist(7L);
        Album album = album(3L, artist);
        TrackCreateUpdateDto dto = new TrackCreateUpdateDto(
                "Digital Love",
                298,
                Genre.ELECTRONIC,
                Mood.CHILL,
                "/tracks/digital-love.mp3",
                3L,
                7L
        );

        when(artistRepository.findById(7L)).thenReturn(Optional.of(artist));
        when(albumRepository.findById(3L)).thenReturn(Optional.of(album));
        when(trackRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TrackDto result = trackService.create(dto, "creator@test.com");

        assertThat(result.albumId()).isEqualTo(3L);
        assertThat(result.artistId()).isEqualTo(7L);
        verify(albumRepository).findById(3L);
    }

    @Test
    void create_whenArtistMissing_shouldThrow() {
        when(artistRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.create(new TrackCreateUpdateDto(
                "Track",
                120,
                Genre.ROCK,
                Mood.HYPE,
                "/tracks/track.mp3",
                null,
                7L
        ), "creator@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("7");

        verify(albumRepository, never()).findById(any());
    }

    @Test
    void create_whenAlbumMissing_shouldThrow() {
        Artist artist = artist(7L);
        when(artistRepository.findById(7L)).thenReturn(Optional.of(artist));
        when(albumRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.create(new TrackCreateUpdateDto(
                "Track",
                120,
                Genre.ROCK,
                Mood.HYPE,
                "/tracks/track.mp3",
                3L,
                7L
        ), "creator@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("3");

        verify(trackRepository, never()).save(any());
    }

    @Test
    void getAll_shouldReturnMappedTracks() {
        Artist artist = artist(7L);
        Album album = album(3L, artist);
        when(trackRepository.findAll()).thenReturn(List.of(
                Track.builder().id(1L).title("Track 1").duration(120).genre(Genre.ROCK).mood(Mood.CHILL)
                        .filePath("one.mp3").artist(artist).album(album).build(),
                Track.builder().id(2L).title("Track 2").duration(180).genre(Genre.POP).mood(Mood.HYPE)
                        .filePath("two.mp3").artist(artist).build()
        ));

        List<TrackDto> result = trackService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).albumId()).isEqualTo(3L);
        assertThat(result.get(1).albumId()).isNull();
    }

    @Test
    void getById_shouldReturnTrack() {
        Artist artist = artist(7L);
        when(trackRepository.findById(1L)).thenReturn(Optional.of(
                Track.builder().id(1L).title("Track 1").duration(120).genre(Genre.ROCK).mood(Mood.CHILL)
                        .filePath("one.mp3").artist(artist).build()
        ));

        TrackDto result = trackService.getById(1L);

        assertThat(result).isEqualTo(
                new TrackDto(1L, "Track 1", 120, Genre.ROCK, Mood.CHILL, "one.mp3", null, 7L, null));
    }

    @Test
    void getById_whenMissing_shouldThrow() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldSaveTrackWithNewAlbum() {
        Artist oldArtist = artist(7L);
        Artist newArtist = artist(8L);
        Album newAlbum = album(3L, newArtist);
        Track track = Track.builder()
                .id(1L)
                .title("Old title")
                .duration(100)
                .genre(Genre.ROCK)
                .mood(Mood.CHILL)
                .filePath("old.mp3")
                .artist(oldArtist)
                .build();
        TrackCreateUpdateDto dto = new TrackCreateUpdateDto(
                "New title",
                200,
                Genre.POP,
                Mood.HYPE,
                "new.mp3",
                3L,
                8L
        );

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(artistRepository.findById(8L)).thenReturn(Optional.of(newArtist));
        when(albumRepository.findById(3L)).thenReturn(Optional.of(newAlbum));
        when(trackRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TrackDto result = trackService.update(1L, dto);

        assertThat(result).isEqualTo(
                new TrackDto(1L, "New title", 200, Genre.POP, Mood.HYPE, "new.mp3", 3L, 8L, null));
        verify(trackRepository).save(track);
    }

    @Test
    void update_shouldAllowNullAlbum() {
        Artist oldArtist = artist(7L);
        Artist newArtist = artist(8L);
        Track track = Track.builder()
                .id(1L)
                .title("Old title")
                .duration(100)
                .genre(Genre.ROCK)
                .mood(Mood.CHILL)
                .filePath("old.mp3")
                .artist(oldArtist)
                .album(album(3L, oldArtist))
                .build();
        TrackCreateUpdateDto dto = new TrackCreateUpdateDto(
                "New title",
                200,
                Genre.POP,
                Mood.HYPE,
                "new.mp3",
                null,
                8L
        );

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(artistRepository.findById(8L)).thenReturn(Optional.of(newArtist));
        when(trackRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TrackDto result = trackService.update(1L, dto);

        assertThat(result.albumId()).isNull();
        assertThat(result.artistId()).isEqualTo(8L);
    }

    @Test
    void update_whenTrackMissing_shouldThrow() {
        when(trackRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.update(1L, new TrackCreateUpdateDto(
                "Title",
                200,
                Genre.POP,
                Mood.HYPE,
                "new.mp3",
                null,
                8L
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void update_whenArtistMissing_shouldThrow() {
        Track track = Track.builder()
                .id(1L)
                .title("Old title")
                .duration(100)
                .genre(Genre.ROCK)
                .mood(Mood.CHILL)
                .filePath("old.mp3")
                .artist(artist(7L))
                .build();

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(artistRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.update(1L, new TrackCreateUpdateDto(
                "Title",
                200,
                Genre.POP,
                Mood.HYPE,
                "new.mp3",
                null,
                8L
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("8");

        verify(trackRepository, never()).save(any());
    }

    @Test
    void update_whenAlbumMissing_shouldThrow() {
        Track track = Track.builder()
                .id(1L)
                .title("Old title")
                .duration(100)
                .genre(Genre.ROCK)
                .mood(Mood.CHILL)
                .filePath("old.mp3")
                .artist(artist(7L))
                .build();

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(artistRepository.findById(8L)).thenReturn(Optional.of(artist(8L)));
        when(albumRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.update(1L, new TrackCreateUpdateDto(
                "Title",
                200,
                Genre.POP,
                Mood.HYPE,
                "new.mp3",
                3L,
                8L
        )))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("3");
    }

    @Test
    void delete_shouldRemoveTrack() {
        Track track = Track.builder()
                .id(1L)
                .title("Track 1")
                .duration(120)
                .genre(Genre.ROCK)
                .mood(Mood.CHILL)
                .filePath("one.mp3")
                .artist(artist(7L))
                .build();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));

        trackService.delete(1L, "admin@test.com", true);

        verify(favoriteRepository).deleteByTrackId(1L);
        verify(playlistRepository).deleteTrackFromPlaylists(1L);
        verify(trackRepository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_shouldThrow() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trackService.delete(99L, "admin@test.com", true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(trackRepository, never()).deleteById(any());
    }

    private Artist artist(final Long id) {
        return Artist.builder()
                .id(id)
                .name("Artist " + id)
                .build();
    }

    private Album album(final Long id, final Artist artist) {
        return Album.builder()
                .id(id)
                .title("Album " + id)
                .artist(artist)
                .build();
    }
}
