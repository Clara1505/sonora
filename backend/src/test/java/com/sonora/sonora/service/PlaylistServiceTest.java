package com.sonora.sonora.service;

import com.sonora.sonora.dto.PlaylistCreateDto;
import com.sonora.sonora.dto.PlaylistDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.model.entity.Playlist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import com.sonora.sonora.model.enums.PlaylistType;
import com.sonora.sonora.model.enums.Role;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock private PlaylistRepository playlistRepository;
    @Mock private TrackRepository    trackRepository;
    @Mock private UserRepository     userRepository;
    @InjectMocks private PlaylistService playlistService;

    private User mockUser() {
        return User.builder().id(1L).email("user@sonora.com").passwordHash("x").role(Role.USER).build();
    }

    private Track mockTrack(Long id, String title, Genre genre, Mood mood) {
        Artist artist = Artist.builder().id(1L).name("Daft Punk").build();
        return Track.builder().id(id).title(title).duration(248).genre(genre).mood(mood).artist(artist).build();
    }

    private Playlist savedPlaylist(String name, PlaylistType type, List<Track> tracks) {
        return Playlist.builder().id(1L).name(name).type(type).user(mockUser()).tracks(tracks).build();
    }


    @Test
    void create_manual_shouldSaveEmptyPlaylist() {
        PlaylistCreateDto dto = new PlaylistCreateDto("Ma playlist", PlaylistType.MANUAL, null, null);
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(mockUser()));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist("Ma playlist", PlaylistType.MANUAL, List.of()));

        PlaylistDto result = playlistService.create(dto, "user@sonora.com");

        assertThat(result.name()).isEqualTo("Ma playlist");
        assertThat(result.type()).isEqualTo(PlaylistType.MANUAL);
        assertThat(result.tracks()).isEmpty();
        verify(playlistRepository).save(any());
        verifyNoInteractions(trackRepository);
    }


    @Test
    void create_smart_withMoodAndGenre_shouldFetchMatchingTracks() {
        Track track = mockTrack(1L, "Get Lucky", Genre.POP, Mood.HAPPY);
        PlaylistCreateDto dto = new PlaylistCreateDto("Pop Vibes", PlaylistType.SMART, Mood.HAPPY, Genre.POP);
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(mockUser()));
        when(trackRepository.findByMoodAndGenre(Mood.HAPPY, Genre.POP)).thenReturn(List.of(track));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist("Pop Vibes", PlaylistType.SMART, List.of(track)));

        PlaylistDto result = playlistService.create(dto, "user@sonora.com");

        assertThat(result.type()).isEqualTo(PlaylistType.SMART);
        assertThat(result.tracks()).hasSize(1);
        assertThat(result.tracks().get(0).title()).isEqualTo("Get Lucky");
        verify(trackRepository).findByMoodAndGenre(Mood.HAPPY, Genre.POP);
        verify(trackRepository, never()).findByGenre(any());
        verify(trackRepository, never()).findByMood(any());
    }


    @Test
    void create_smart_withGenreOnly_shouldFetchByGenre() {
        Track track = mockTrack(1L, "Bohemian Rhapsody", Genre.ROCK, Mood.HYPE);
        PlaylistCreateDto dto = new PlaylistCreateDto("Rock Only", PlaylistType.SMART, null, Genre.ROCK);
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(mockUser()));
        when(trackRepository.findByGenre(Genre.ROCK)).thenReturn(List.of(track));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist("Rock Only", PlaylistType.SMART, List.of(track)));

        PlaylistDto result = playlistService.create(dto, "user@sonora.com");

        assertThat(result.tracks()).hasSize(1);
        verify(trackRepository).findByGenre(Genre.ROCK);
        verify(trackRepository, never()).findByMoodAndGenre(any(), any());
        verify(trackRepository, never()).findByMood(any());
    }


    @Test
    void create_smart_withMoodOnly_shouldFetchByMood() {
        Track track = mockTrack(1L, "So What", Genre.JAZZ, Mood.CHILL);
        PlaylistCreateDto dto = new PlaylistCreateDto("Chill Vibes", PlaylistType.SMART, Mood.CHILL, null);
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(mockUser()));
        when(trackRepository.findByMood(Mood.CHILL)).thenReturn(List.of(track));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist("Chill Vibes", PlaylistType.SMART, List.of(track)));

        PlaylistDto result = playlistService.create(dto, "user@sonora.com");

        assertThat(result.tracks()).hasSize(1);
        verify(trackRepository).findByMood(Mood.CHILL);
        verify(trackRepository, never()).findByMoodAndGenre(any(), any());
        verify(trackRepository, never()).findByGenre(any());
    }


    @Test
    void create_smart_withNoCriteria_shouldSaveEmptyPlaylist() {
        PlaylistCreateDto dto = new PlaylistCreateDto("Empty Smart", PlaylistType.SMART, null, null);
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(mockUser()));
        when(playlistRepository.save(any())).thenReturn(savedPlaylist("Empty Smart", PlaylistType.SMART, List.of()));

        PlaylistDto result = playlistService.create(dto, "user@sonora.com");

        assertThat(result.tracks()).isEmpty();
        verifyNoInteractions(trackRepository);
    }


    @Test
    void create_whenUserNotFound_shouldThrow() {
        PlaylistCreateDto dto = new PlaylistCreateDto("X", PlaylistType.MANUAL, null, null);
        when(userRepository.findByEmail("unknown@sonora.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.create(dto, "unknown@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown@sonora.com");
    }


    @Test
    void getUserPlaylists_shouldReturnAllUserPlaylists() {
        Playlist p1 = savedPlaylist("Playlist 1", PlaylistType.MANUAL, List.of());
        Playlist p2 = savedPlaylist("Playlist 2", PlaylistType.SMART, List.of());
        when(playlistRepository.findByUserEmail("user@sonora.com")).thenReturn(List.of(p1, p2));

        List<PlaylistDto> result = playlistService.getUserPlaylists("user@sonora.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Playlist 1");
        assertThat(result.get(1).name()).isEqualTo("Playlist 2");
    }


    @Test
    void getById_shouldReturnPlaylist() {
        Playlist playlist = savedPlaylist("Ma playlist", PlaylistType.MANUAL, List.of());
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        PlaylistDto result = playlistService.getById(1L, "user@sonora.com");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Ma playlist");
    }


    @Test
    void getById_whenNotOwner_shouldThrow() {
        Playlist playlist = savedPlaylist("Ma playlist", PlaylistType.MANUAL, List.of());
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        assertThatThrownBy(() -> playlistService.getById(1L, "other@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void getById_whenNotFound_shouldThrow() {
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.getById(99L, "user@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void delete_shouldRemovePlaylist() {
        Playlist playlist = savedPlaylist("Ma playlist", PlaylistType.MANUAL, List.of());
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        playlistService.delete(1L, "user@sonora.com");

        verify(playlistRepository).delete(playlist);
    }


    @Test
    void delete_whenNotOwner_shouldThrow() {
        Playlist playlist = savedPlaylist("Ma playlist", PlaylistType.MANUAL, List.of());
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

        assertThatThrownBy(() -> playlistService.delete(1L, "other@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(playlistRepository, never()).delete(any());
    }


    @Test
    void delete_whenNotFound_shouldThrow() {
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playlistService.delete(99L, "user@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
