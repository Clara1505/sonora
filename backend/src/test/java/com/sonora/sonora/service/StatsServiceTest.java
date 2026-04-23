package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackStatsResponse;
import com.sonora.sonora.dto.UserStatsResponse;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.*;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.PlayHistoryRepository;
import com.sonora.sonora.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private PlayHistoryRepository playHistoryRepository;
    @Mock private TrackRepository       trackRepository;
    @InjectMocks private StatsService statsService;

    private Track mockTrack(Long id, String title) {
        return Track.builder().id(id).title(title)
                .artist(Artist.builder().name("Artiste").build()).build();
    }

    private User mockUser() {
        return User.builder().id(1L).email("user@sonora.com").role(Role.USER).build();
    }


    @Test
    void getTrackStats_shouldReturnPlayCountAndDuration() {
        Track track = mockTrack(1L, "Ma chanson");
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(playHistoryRepository.countByTrackId(1L)).thenReturn(5L);
        when(playHistoryRepository.sumDurationByTrackId(1L)).thenReturn(1200L);

        TrackStatsResponse response = statsService.getTrackStats(1L);

        assertThat(response.getPlayCount()).isEqualTo(5L);
        assertThat(response.getTotalDurationListened()).isEqualTo(1200L);
        assertThat(response.getTrackTitle()).isEqualTo("Ma chanson");
    }


    @Test
    void getTrackStats_withUnknownId_shouldThrow404() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statsService.getTrackStats(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void getUserStats_shouldReturnTotalPlaysAndTopTracks() {
        User user   = mockUser();
        Track track = mockTrack(1L, "Ma chanson");

        PlayHistory h1 = PlayHistory.builder().user(user).track(track).durationListened(200).build();
        PlayHistory h2 = PlayHistory.builder().user(user).track(track).durationListened(180).build();

        when(playHistoryRepository.findByUserEmail("user@sonora.com")).thenReturn(List.of(h1, h2));

        UserStatsResponse response = statsService.getUserStats("user@sonora.com");

        assertThat(response.getTotalPlays()).isEqualTo(2);
        assertThat(response.getTopTracks()).hasSize(1);
        assertThat(response.getTopTracks().get(0).getPlayCount()).isEqualTo(2);
        assertThat(response.getTopTracks().get(0).getTotalDurationListened()).isEqualTo(380L);
    }
}