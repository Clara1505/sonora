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
class TrackEventProducerTest {

    @Mock    private KafkaTemplate<String, TrackImportedEvent> kafkaTemplate;
    @InjectMocks private TrackEventProducer producer;

    @Test
    void sendTrackImported_shouldPublishToCorrectTopicWithTrackIdAsKey() {
        TrackImportedEvent event = TrackImportedEvent.builder()
                .trackId(42L)
                .filePath("uuid.mp3")
                .title("Ma chanson")
                .uploadedByEmail("user@sonora.com")
                .build();

        producer.sendTrackImported(event);

        ArgumentCaptor<String>             topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String>             keyCaptor   = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TrackImportedEvent> eventCaptor = ArgumentCaptor.forClass(TrackImportedEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("track.imported");
        assertThat(keyCaptor.getValue()).isEqualTo("42");
        assertThat(eventCaptor.getValue().getUploadedByEmail()).isEqualTo("user@sonora.com");
    }
}