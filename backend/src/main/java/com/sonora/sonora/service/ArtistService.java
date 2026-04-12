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

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistDto create(ArtistCreateUpdateDto dto) {
        Artist artist = Artist.builder()
                .name(dto.name())
                .bio(dto.bio())
                .imageUrl(dto.imageUrl())
                .build();

        return toDto(artistRepository.save(artist));
    }

    @Transactional(readOnly = true)
    public List<ArtistDto> getAll() {
        return artistRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArtistDto getById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + id));
        return toDto(artist);
    }

    public ArtistDto update(Long id, ArtistCreateUpdateDto dto) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + id));

        artist.setName(dto.name());
        artist.setBio(dto.bio());
        artist.setImageUrl(dto.imageUrl());

        return toDto(artistRepository.save(artist));
    }

    public void delete(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artist not found: " + id);
        }
        artistRepository.deleteById(id);
    }

    private ArtistDto toDto(Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getName(),
                artist.getBio(),
                artist.getImageUrl()
        );
    }
}
