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

/** Service handling playlist lifecycle operations. */
@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    /** Repository for playlist persistence operations. */
    private final PlaylistRepository playlistRepository;
    /** Repository for track lookups. */
    private final TrackRepository trackRepository;
    /** Repository for user lookups. */
    private final UserRepository userRepository;

    /**
     * Creates a playlist for a user.
     *
     * @param dto creation payload
     * @param userEmail owner email
     * @return created playlist
     */
    public PlaylistDto create(final PlaylistCreateDto dto,
                              final String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found: " + userEmail));

        List<Track> tracks = new ArrayList<>();
        if (dto.type() == PlaylistType.SMART) {
            boolean hasMood = dto.mood() != null;
            boolean hasGenre = dto.genre() != null;
            if (hasMood && hasGenre) {
                tracks = trackRepository.findByMoodAndGenre(
                        dto.mood(),
                        dto.genre()
                );
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

    /**
     * Returns playlists owned by a user.
     *
     * @param userEmail owner email
     * @return list of playlists
     */
    @Transactional(readOnly = true)
    public List<PlaylistDto> getUserPlaylists(final String userEmail) {
        return playlistRepository.findByUserEmail(userEmail).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Returns a user-owned playlist by identifier.
     *
     * @param id playlist identifier
     * @param userEmail owner email
     * @return playlist response
     */
    @Transactional(readOnly = true)
    public PlaylistDto getById(final Long id, final String userEmail) {
        Playlist playlist = findOwnedPlaylist(id, userEmail);
        return toDto(playlist);
    }

    /**
     * Deletes a user-owned playlist.
     *
     * @param id playlist identifier
     * @param userEmail owner email
     */
    public void delete(final Long id, final String userEmail) {
        Playlist playlist = findOwnedPlaylist(id, userEmail);
        playlistRepository.delete(playlist);
    }

    /**
     * Adds a track to a user-owned playlist.
     *
     * @param playlistId playlist identifier
     * @param trackId track identifier
     * @param userEmail owner email
     * @return updated playlist
     */
    public PlaylistDto addTrack(final Long playlistId,
                                final Long trackId,
                                final String userEmail) {
        Playlist playlist = findOwnedPlaylist(playlistId, userEmail);
        Track track = trackRepository.findById(trackId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Track not found: " + trackId));
        if (!playlist.getTracks().contains(track)) {
            playlist.getTracks().add(track);
        }
        return toDto(playlistRepository.save(playlist));
    }

    /**
     * Removes a track from a user-owned playlist.
     *
     * @param playlistId playlist identifier
     * @param trackId track identifier
     * @param userEmail owner email
     * @return updated playlist
     */
    public PlaylistDto removeTrack(final Long playlistId,
                                   final Long trackId,
                                   final String userEmail) {
        Playlist playlist = findOwnedPlaylist(playlistId, userEmail);
        playlist.getTracks().removeIf(t -> t.getId().equals(trackId));
        return toDto(playlistRepository.save(playlist));
    }

    /**
     * Resolves a playlist and ensures ownership.
     *
     * @param id playlist identifier
     * @param userEmail owner email
     * @return owned playlist entity
     */
    private Playlist findOwnedPlaylist(final Long id, final String userEmail) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found: " + id));
        if (!playlist.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Playlist not found: " + id);
        }
        return playlist;
    }

    /**
     * Maps playlist entity to DTO.
     *
     * @param playlist source playlist
     * @return mapped DTO
     */
    private PlaylistDto toDto(final Playlist playlist) {
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
