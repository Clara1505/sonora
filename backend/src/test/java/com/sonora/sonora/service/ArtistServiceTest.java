package com.sonora.sonora.service;

import com.sonora.sonora.dto.ArtistCreateUpdateDto;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Artist;
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
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void create_shouldSaveAndReturnArtist() {
        ArtistCreateUpdateDto dto = new ArtistCreateUpdateDto(
                "Daft Punk",
                "French electronic duo",
                "https://sonora.dev/daft-punk.png"
        );

        when(artistRepository.save(any())).thenAnswer(invocation -> {
            Artist artist = invocation.getArgument(0);
            return Artist.builder()
                    .id(1L)
                    .name(artist.getName())
                    .bio(artist.getBio())
                    .imageUrl(artist.getImageUrl())
                    .build();
        });

        ArtistDto result = artistService.create(dto);

        assertThat(result).isEqualTo(new ArtistDto(
                1L,
                "Daft Punk",
                "French electronic duo",
                "https://sonora.dev/daft-punk.png"
        ));
        verify(artistRepository).save(any());
    }

    @Test
    void getAll_shouldReturnMappedArtists() {
        when(artistRepository.findAll()).thenReturn(List.of(
                Artist.builder().id(1L).name("Daft Punk").bio("Duo").imageUrl("one").build(),
                Artist.builder().id(2L).name("Justice").bio("Duo").imageUrl("two").build()
        ));

        List<ArtistDto> result = artistService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Daft Punk");
        assertThat(result.get(1).name()).isEqualTo("Justice");
    }

    @Test
    void getById_shouldReturnArtist() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(
                Artist.builder().id(1L).name("Daft Punk").bio("Duo").imageUrl("one").build()
        ));

        ArtistDto result = artistService.getById(1L);

        assertThat(result).isEqualTo(new ArtistDto(1L, "Daft Punk", "Duo", "one"));
    }

    @Test
    void getById_whenMissing_shouldThrow() {
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldSaveUpdatedArtist() {
        Artist artist = Artist.builder()
                .id(1L)
                .name("Old name")
                .bio("Old bio")
                .imageUrl("old.png")
                .build();
        ArtistCreateUpdateDto dto = new ArtistCreateUpdateDto(
                "New name",
                "New bio",
                "new.png"
        );

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ArtistDto result = artistService.update(1L, dto);

        assertThat(result).isEqualTo(new ArtistDto(1L, "New name", "New bio", "new.png"));
        verify(artistRepository).save(artist);
    }

    @Test
    void update_whenMissing_shouldThrow() {
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.update(99L, new ArtistCreateUpdateDto("Name", null, null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(artistRepository, never()).save(any());
    }

    @Test
    void delete_shouldRemoveArtist() {
        when(artistRepository.existsById(1L)).thenReturn(true);

        artistService.delete(1L);

        verify(artistRepository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_shouldThrow() {
        when(artistRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> artistService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(artistRepository, never()).deleteById(any());
    }
}