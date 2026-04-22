package com.sonora.sonora.service;

import com.sonora.sonora.dto.ArtistCreateUpdateDto;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Service handling artist CRUD operations. */
@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    /** Repository for artist persistence operations. */
    private final ArtistRepository artistRepository;

    /**
     * Creates a new artist.
     *
     * @param dto artist payload
     * @return created artist
     */
    public ArtistDto create(final ArtistCreateUpdateDto dto) {
        Artist artist = Artist.builder()
                .name(dto.name())
                .bio(dto.bio())
                .imageUrl(dto.imageUrl())
                .build();

        return toDto(artistRepository.save(artist));
    }

    /**
     * Returns all artists.
     *
     * @return list of artists
     */
    @Transactional(readOnly = true)
    public List<ArtistDto> getAll() {
        return artistRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Returns an artist by identifier.
     *
     * @param id artist identifier
     * @return matching artist
     */
    @Transactional(readOnly = true)
    public ArtistDto getById(final Long id) {
        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Artist not found: " + id));
        return toDto(artist);
    }

    /**
     * Updates an artist.
     *
     * @param id artist identifier
     * @param dto update payload
     * @return updated artist
     */
    public ArtistDto update(final Long id, final ArtistCreateUpdateDto dto) {
        Artist artist = artistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Artist not found: " + id));

        artist.setName(dto.name());
        artist.setBio(dto.bio());
        artist.setImageUrl(dto.imageUrl());

        return toDto(artistRepository.save(artist));
    }

    /**
     * Deletes an artist.
     *
     * @param id artist identifier
     */
    public void delete(final Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artist not found: " + id);
        }
        artistRepository.deleteById(id);
    }

    /**
     * Maps an artist entity to DTO.
     *
     * @param artist source entity
     * @return mapped DTO
     */
    private ArtistDto toDto(final Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getName(),
                artist.getBio(),
                artist.getImageUrl()
        );
    }
}
