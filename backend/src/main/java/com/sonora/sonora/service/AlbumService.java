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

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumDto create(AlbumCreateUpdateDto dto) {
        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + dto.artistId()));

        Album album = Album.builder()
                .title(dto.title())
                .releaseYear(dto.releaseYear())
                .coverUrl(dto.coverUrl())
                .artist(artist)
                .build();

        return toDto(albumRepository.save(album));
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAll() {
        return albumRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlbumDto getById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found: " + id));
        return toDto(album);
    }

    public AlbumDto update(Long id, AlbumCreateUpdateDto dto) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found: " + id));

        Artist artist = artistRepository.findById(dto.artistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + dto.artistId()));

        album.setTitle(dto.title());
        album.setReleaseYear(dto.releaseYear());
        album.setCoverUrl(dto.coverUrl());
        album.setArtist(artist);

        return toDto(albumRepository.save(album));
    }

    public void delete(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Album not found: " + id);
        }
        albumRepository.deleteById(id);
    }

    private AlbumDto toDto(Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                album.getReleaseYear(),
                album.getCoverUrl(),
                album.getArtist().getId()
        );
    }
}
