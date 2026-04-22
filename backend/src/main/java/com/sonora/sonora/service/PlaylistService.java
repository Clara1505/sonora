package com.sonora.sonora.service;

import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.dto.PlaylistCreateDto;
import com.sonora.sonora.dto.PlaylistDto;
import com.sonora.sonora.dto.PlaylistTrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Playlist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.model.enums.PlaylistType;
import com.sonora.sonora.repository.PlaylistRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public PlaylistDto create(PlaylistCreateDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        List<Track> tracks = new ArrayList<>();
        if (dto.type() == PlaylistType.SMART) {
            boolean hasMood = dto.mood() != null;
            boolean hasGenre = dto.genre() != null;
            if (hasMood && hasGenre) {
                tracks = trackRepository.findByMoodAndGenre(dto.mood(), dto.genre());
            } else if (hasGenre) {
                tracks = trackRepository.findByGenre(dto.genre());
            } else if (hasMood) {
                tracks = trackRepository.findByMood(dto.mood());
            }
        }

        Playlist playlist = Playlist.builder()
                .name(dto.name())
                .type(dto.type())
                .user(user)
                .tracks(tracks)
                .build();

        return toDto(playlistRepository.save(playlist));
    }

    @Transactional(readOnly = true)
    public List<PlaylistDto> getUserPlaylists(String userEmail) {
        return playlistRepository.findByUserEmail(userEmail).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlaylistDto getById(Long id, String userEmail) {
        Playlist playlist = findOwnedPlaylist(id, userEmail);
        return toDto(playlist);
    }

    public void delete(Long id, String userEmail) {
        Playlist playlist = findOwnedPlaylist(id, userEmail);
        playlistRepository.delete(playlist);
    }

    public PlaylistDto addTrack(Long playlistId, Long trackId, String userEmail) {
        Playlist playlist = findOwnedPlaylist(playlistId, userEmail);
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found: " + trackId));
        if (!playlist.getTracks().contains(track)) {
            playlist.getTracks().add(track);
        }
        return toDto(playlistRepository.save(playlist));
    }

    public PlaylistDto removeTrack(Long playlistId, Long trackId, String userEmail) {
        Playlist playlist = findOwnedPlaylist(playlistId, userEmail);
        playlist.getTracks().removeIf(t -> t.getId().equals(trackId));
        return toDto(playlistRepository.save(playlist));
    }

    private Playlist findOwnedPlaylist(Long id, String userEmail) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + id));
        if (!playlist.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Playlist not found: " + id);
        }
        return playlist;
    }

    private PlaylistDto toDto(Playlist playlist) {
        List<PlaylistTrackDto> tracks = playlist.getTracks().stream()
                .map(t -> new PlaylistTrackDto(
                        t.getId(),
                        t.getTitle(),
                        t.getDuration(),
                        t.getGenre(),
                        t.getMood(),
                        new ArtistDto(
                                t.getArtist().getId(),
                                t.getArtist().getName(),
                                t.getArtist().getBio(),
                                t.getArtist().getImageUrl()
                        )
                ))
                .toList();

        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getType(),
                playlist.getImageUrl(),
                playlist.getCreatedAt(),
                tracks
        );
    }
}
