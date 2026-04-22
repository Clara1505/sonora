package com.sonora.sonora.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Producer publishing track played events to Kafka. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedProducer {

    /** Kafka topic used for played track events. */
    static final String TOPIC_TRACK_PLAYED = "track.played";

    /** Kafka template used to publish track played events. */
    private final KafkaTemplate<String, TrackPlayedEvent> kafkaTemplate;

    /**
     * Sends a track played event.
     *
     * @param event event payload
     */
    public void sendTrackPlayed(final TrackPlayedEvent event) {
        log.info("Publication Kafka [{}] : trackId={}, user={}",
                TOPIC_TRACK_PLAYED, event.getTrackId(), event.getUserEmail());
        kafkaTemplate.send(
            TOPIC_TRACK_PLAYED,
            String.valueOf(event.getTrackId()),
            event
        );
    }
}
