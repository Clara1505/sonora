package com.sonora.sonora.service;

import com.sonora.sonora.dto.AlbumCreateUpdateDto;
import com.sonora.sonora.dto.AlbumDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Album;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.repository.AlbumRepository;
import com.sonora.sonora.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Service handling album CRUD operations. */
@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {

    /** Repository for album persistence operations. */
    private final AlbumRepository albumRepository;

    /** Repository for artist lookups. */
    private final ArtistRepository artistRepository;

    /**
     * Creates a new album.
     *
     * @param dto album creation payload
     * @return created album
     */
    public AlbumDto create(final AlbumCreateUpdateDto dto) {
        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist not found: " + dto.artistId()));

        Album album = Album.builder()
                .title(dto.title())
                .releaseYear(dto.releaseYear())
                .coverUrl(dto.coverUrl())
                .artist(artist)
                .build();

        return toDto(albumRepository.save(album));
    }

    /**
     * Returns all albums.
     *
     * @return list of albums
     */
    @Transactional(readOnly = true)
    public List<AlbumDto> getAll() {
        return albumRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Returns a single album by identifier.
     *
     * @param id album identifier
     * @return matching album
     */
    @Transactional(readOnly = true)
    public AlbumDto getById(final Long id) {
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Album not found: " + id));
        return toDto(album);
    }

    /**
     * Updates an existing album.
     *
     * @param id album identifier
     * @param dto update payload
     * @return updated album
     */
    public AlbumDto update(final Long id, final AlbumCreateUpdateDto dto) {
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Album not found: " + id));

        Artist artist = artistRepository.findById(dto.artistId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Artist not found: " + dto.artistId()));

        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        album.setCoverUrl(dto.coverUrl());
        album.setArtist(artist);

        return toDto(albumRepository.save(album));
    }

    /**
     * Deletes an album by identifier.
     *
     * @param id album identifier
     */
    public void delete(final Long id) {
        if (!albumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Album not found: " + id);
        }
        albumRepository.deleteById(id);
    }

    /**
     * Maps an album entity to DTO.
     *
     * @param album source entity
     * @return mapped DTO
     */
    private AlbumDto toDto(final Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                album.getReleaseYear(),
                album.getCoverUrl(),
                album.getArtist().getId()
        );
    }
}
