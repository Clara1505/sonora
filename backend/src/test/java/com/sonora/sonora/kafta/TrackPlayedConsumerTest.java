package com.sonora.sonora.kafka;

import com.sonora.sonora.model.entity.*;
import com.sonora.sonora.model.enums.Role;
import com.sonora.sonora.repository.PlayHistoryRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackPlayedConsumerTest {

    @Mock private PlayHistoryRepository playHistoryRepository;
    @Mock private TrackRepository       trackRepository;
    @Mock private UserRepository        userRepository;
    @InjectMocks private TrackPlayedConsumer consumer;

    @Test
    void consume_withValidEvent_shouldSavePlayHistory() {
        Track track = Track.builder().id(1L).title("Ma chanson")
                .artist(Artist.builder().name("Artiste").build()).build();
        User user = User.builder().id(1L).email("user@sonora.com").role(Role.USER).build();

        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(userRepository.findByEmail("user@sonora.com")).thenReturn(Optional.of(user));

        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(1L)
                .userEmail("user@sonora.com")
                .durationListened(180)
                .build();

        consumer.consume(event);

        ArgumentCaptor<PlayHistory> captor = ArgumentCaptor.forClass(PlayHistory.class);
        verify(playHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getTrack().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getUser().getEmail()).isEqualTo("user@sonora.com");
        assertThat(captor.getValue().getDurationListened()).isEqualTo(180);
    }

    @Test
    void consume_withUnknownTrack_shouldNotSave() {
        when(trackRepository.findById(99L)).thenReturn(Optional.empty());

        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(99L).userEmail("user@sonora.com").durationListened(100).build();

        consumer.consume(event);

        verifyNoInteractions(playHistoryRepository);
    }

    @Test
    void consume_withUnknownUser_shouldNotSave() {
        Track track = Track.builder().id(1L).title("Ma chanson")
                .artist(Artist.builder().name("Artiste").build()).build();
        when(trackRepository.findById(1L)).thenReturn(Optional.of(track));
        when(userRepository.findByEmail("unknown@sonora.com")).thenReturn(Optional.empty());

        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(1L).userEmail("unknown@sonora.com").durationListened(100).build();

        consumer.consume(event);

        verifyNoInteractions(playHistoryRepository);
    }
}