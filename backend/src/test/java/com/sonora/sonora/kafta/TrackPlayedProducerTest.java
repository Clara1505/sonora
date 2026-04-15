package com.sonora.sonora.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackPlayedProducerTest {

    @Mock
    private KafkaTemplate<String, TrackPlayedEvent> kafkaTemplate;

    @InjectMocks
    private TrackPlayedProducer producer;

    @Test
    void sendTrackPlayed_shouldPublishToCorrectTopicWithTrackIdAsKey() {
        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(7L)
                .userEmail("listener@sonora.com")
                .durationListened(123)
                .build();

        producer.sendTrackPlayed(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TrackPlayedEvent> eventCaptor = ArgumentCaptor.forClass(TrackPlayedEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("track.played");
        assertThat(keyCaptor.getValue()).isEqualTo("7");
        assertThat(eventCaptor.getValue().getUserEmail()).isEqualTo("listener@sonora.com");
    }
}
