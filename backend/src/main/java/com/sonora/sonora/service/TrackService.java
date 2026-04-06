package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Album;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.repository.AlbumRepository;
import com.sonora.sonora.repository.ArtistRepository;
import com.sonora.sonora.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    public TrackDto create(TrackCreateUpdateDto dto) {
        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + dto.artistId()));

        Album album = resolveAlbum(dto.albumId());

        Track track = Track.builder()
                .title(dto.title())
                .duration(dto.duration())
                .genre(dto.genre())
                .mood(dto.mood())
                .filePath(dto.filePath())
                .artist(artist)
                .album(album)
                .build();

        return toDto(trackRepository.save(track));
    }

    @Transactional(readOnly = true)
    public List<TrackDto> getAll() {
        return trackRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrackDto getById(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found: " + id));
        return toDto(track);
    }

    public TrackDto update(Long id, TrackCreateUpdateDto dto) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found: " + id));

        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + dto.artistId()));

        Album album = resolveAlbum(dto.albumId());

        track.setTitle(dto.title());
        track.setDuration(dto.duration());
        track.setGenre(dto.genre());
        track.setMood(dto.mood());
        track.setFilePath(dto.filePath());
        track.setArtist(artist);
        track.setAlbum(album);

        return toDto(trackRepository.save(track));
    }

    public void delete(Long id) {
        if (!trackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Track not found: " + id);
        }
        trackRepository.deleteById(id);
    }

    private Album resolveAlbum(Long albumId) {
        if (albumId == null) {
            return null;
        }
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found: " + albumId));
    }

    private TrackDto toDto(Track track) {
        return new TrackDto(
                track.getId(),
                track.getTitle(),
                track.getDuration(),
                track.getGenre(),
                track.getMood(),
                track.getFilePath(),
                track.getAlbum() != null ? track.getAlbum().getId() : null,
                track.getArtist().getId()
        );
    }
}
