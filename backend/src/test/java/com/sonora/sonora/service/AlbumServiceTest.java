package com.sonora.sonora.service;

import com.sonora.sonora.dto.AlbumCreateUpdateDto;
import com.sonora.sonora.dto.AlbumDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Album;
import com.sonora.sonora.model.entity.Artist;
import com.sonora.sonora.repository.AlbumRepository;
import com.sonora.sonora.repository.ArtistRepository;
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
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void create_shouldSaveAlbumWithArtist() {
        AlbumCreateUpdateDto dto = new AlbumCreateUpdateDto(
                "Discovery",
                2001,
                "https://sonora.dev/discovery.png",
                7L
        );
        Artist artist = Artist.builder().id(7L).name("Daft Punk").build();

        when(artistRepository.findById(7L)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any())).thenAnswer(invocation -> {
            Album album = invocation.getArgument(0);
            return Album.builder()
                    .id(11L)
                    .title(album.getTitle())
                    .releaseYear(album.getReleaseYear())
                    .coverUrl(album.getCoverUrl())
                    .artist(album.getArtist())
                    .build();
        });

        AlbumDto result = albumService.create(dto);

        assertThat(result).isEqualTo(new AlbumDto(
                11L,
                "Discovery",
                2001,
                "https://sonora.dev/discovery.png",
                7L
        ));
        verify(albumRepository).save(any());
    }

    @Test
    void create_whenArtistMissing_shouldThrow() {
        when(artistRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.create(new AlbumCreateUpdateDto("Discovery", 2001, null, 7L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("7");
    }

    @Test
    void getAll_shouldReturnMappedAlbums() {
        Artist artist = Artist.builder().id(7L).name("Daft Punk").build();
        when(albumRepository.findAll()).thenReturn(List.of(
                Album.builder().id(1L).title("Discovery").releaseYear(2001).coverUrl("one").artist(artist).build(),
                Album.builder().id(2L).title("Human After All").releaseYear(2005).coverUrl("two").artist(artist).build()
        ));

        List<AlbumDto> result = albumService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).artistId()).isEqualTo(7L);
        assertThat(result.get(1).title()).isEqualTo("Human After All");
    }

    @Test
    void getById_shouldReturnAlbum() {
        Artist artist = Artist.builder().id(7L).name("Daft Punk").build();
        when(albumRepository.findById(1L)).thenReturn(Optional.of(
                Album.builder().id(1L).title("Discovery").releaseYear(2001).coverUrl("one").artist(artist).build()
        ));

        AlbumDto result = albumService.getById(1L);

        assertThat(result).isEqualTo(new AlbumDto(1L, "Discovery", 2001, "one", 7L));
    }

    @Test
    void getById_whenMissing_shouldThrow() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldSaveUpdatedAlbum() {
        Artist originalArtist = Artist.builder().id(7L).name("Daft Punk").build();
        Artist newArtist = Artist.builder().id(8L).name("Justice").build();
        Album album = Album.builder()
                .id(1L)
                .title("Old title")
                .releaseYear(1999)
                .coverUrl("old.png")
                .artist(originalArtist)
                .build();
        AlbumCreateUpdateDto dto = new AlbumCreateUpdateDto("New title", 2005, "new.png", 8L);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(8L)).thenReturn(Optional.of(newArtist));
        when(albumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AlbumDto result = albumService.update(1L, dto);

        assertThat(result).isEqualTo(new AlbumDto(1L, "New title", 2005, "new.png", 8L));
        verify(albumRepository).save(album);
    }

    @Test
    void update_whenAlbumMissing_shouldThrow() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(1L, new AlbumCreateUpdateDto("Title", 2001, null, 7L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album not found: 1");

        verify(artistRepository, never()).findById(any());
    }

    @Test
    void update_whenArtistMissing_shouldThrow() {
        Album album = Album.builder()
                .id(1L)
                .title("Old title")
                .releaseYear(1999)
                .coverUrl("old.png")
                .artist(Artist.builder().id(7L).name("Daft Punk").build())
                .build();

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(artistRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(1L, new AlbumCreateUpdateDto("Title", 2001, null, 8L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("8");

        verify(albumRepository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveAlbum() {
        when(albumRepository.existsById(1L)).thenReturn(true);

        albumService.delete(1L);

        verify(albumRepository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_shouldThrow() {
        when(albumRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> albumService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(albumRepository, never()).deleteById(any());
    }
}