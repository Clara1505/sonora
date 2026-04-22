package com.sonora.sonora.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Producer publishing track import events to Kafka. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrackEventProducer {

    /** Kafka topic used for imported track events. */
    static final String TOPIC_TRACK_IMPORTED = "track.imported";

    /** Kafka template used to publish track imported events. */
    private final KafkaTemplate<String, TrackImportedEvent> kafkaTemplate;

    /**
     * Sends a track imported event.
     *
     * @param event event payload
     */
    public void sendTrackImported(final TrackImportedEvent event) {
        log.info(
            "Publication Kafka [{}] : trackId={}",
            TOPIC_TRACK_IMPORTED,
            event.getTrackId()
        );
        kafkaTemplate.send(
            TOPIC_TRACK_IMPORTED,
            String.valueOf(event.getTrackId()),
            event
        );
    }
}
