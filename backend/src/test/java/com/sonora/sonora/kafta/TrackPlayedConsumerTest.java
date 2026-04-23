package com.sonora.sonora.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackPlayedConsumerTest {

    @InjectMocks private TrackPlayedConsumer consumer;

    @Test
    void consume_withValidEvent_shouldLogWithoutException() {
        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(1L)
                .userEmail("user@sonora.com")
                .durationListened(180)
                .build();

        consumer.consume(event);
    }

    @Test
    void consume_withNullDuration_shouldNotThrow() {
        TrackPlayedEvent event = TrackPlayedEvent.builder()
                .trackId(1L)
                .userEmail("user@sonora.com")
                .durationListened(null)
                .build();

        consumer.consume(event);
    }
}
