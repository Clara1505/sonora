package com.sonora.sonora.service;

import com.sonora.sonora.dto.FavoriteResponse;
import com.sonora.sonora.exception.AlreadyFavoriteException;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.*;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.FavoriteRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private TrackRepository    trackRepository;
    @Mock private UserRepository     userRepository;
    @InjectMocks private FavoriteService favoriteService;

    private User mockUser() {
        return User.builder().id(1L).email("user@sonora.com").role(Role.USER).build();
    }

    private Track mockTrack() {
        Artist artist = Artist.builder().id(1L).name("Artiste").build();
        return Track.builder().id(10L).title("Ma chanson").artist(artist).build();
    }


    @Test
    void addFavorite_shouldSaveAndReturnResponse() {
        User user   = mockUser();
        Track track = mockTrack();

        when(favoriteRepository.existsByUserEmailAndTrackId("user@sonora.com", 10L)).thenReturn(false);
        when(trackRepository.findById(10L)).thenReturn(Optional.of(track));
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(user));
        when(favoriteRepository.save(any())).thenAnswer(inv -> {
            Favorite f = inv.getArgument(0);
            f = Favorite.builder().id(1L).user(f.getUser()).track(f.getTrack()).build();
            return f;
        });

        FavoriteResponse response = favoriteService.addFavorite(10L, "user@sonora.com");

        assertThat(response.getTrackId()).isEqualTo(10L);
        assertThat(response.getTrackTitle()).isEqualTo("Ma chanson");
        verify(favoriteRepository).save(any());
    }

    

    @Test
    void addFavorite_whenAlreadyFavorite_shouldThrow409() {
        when(favoriteRepository.existsByUserEmailAndTrackId("user@sonora.com", 10L)).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(10L, "user@sonora.com"))
                .isInstanceOf(AlreadyFavoriteException.class)
                .hasMessageContaining("10");

        verifyNoMoreInteractions(trackRepository, userRepository);
    }

    

    @Test
    void removeFavorite_shouldDeleteFavorite() {
        Favorite favorite = Favorite.builder()
                .id(1L).user(mockUser()).track(mockTrack()).build();

        when(favoriteRepository.findByUserEmailAndTrackId("user@sonora.com", 10L))
                .thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(10L, "user@sonora.com");

        verify(favoriteRepository).delete(favorite);
    }


    @Test
    void removeFavorite_whenNotFound_shouldThrow404() {
        when(favoriteRepository.findByUserEmailAndTrackId("user@sonora.com", 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.removeFavorite(99L, "user@sonora.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void listFavorites_shouldReturnAllFavorites() {
        Favorite f1 = Favorite.builder().id(1L).user(mockUser()).track(mockTrack()).build();
        Favorite f2 = Favorite.builder().id(2L).user(mockUser())
                .track(Track.builder().id(20L).title("Autre chanson")
                        .artist(Artist.builder().name("Artiste 2").build()).build())
                .build();

        when(favoriteRepository.findByUserEmail("user@sonora.com")).thenReturn(List.of(f1, f2));

        List<FavoriteResponse> result = favoriteService.listFavorites("user@sonora.com");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTrackId()).isEqualTo(10L);
        assertThat(result.get(1).getTrackId()).isEqualTo(20L);
    }
}