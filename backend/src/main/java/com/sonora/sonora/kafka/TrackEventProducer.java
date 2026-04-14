package com.sonora.sonora.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackEventProducer {

    static final String TOPIC_TRACK_IMPORTED = "track.imported";

    private final KafkaTemplate<String, TrackImportedEvent> kafkaTemplate;

    public void sendTrackImported(TrackImportedEvent event) {
        log.info("Publication Kafka [{}] : trackId={}", TOPIC_TRACK_IMPORTED, event.getTrackId());
        kafkaTemplate.send(TOPIC_TRACK_IMPORTED, String.valueOf(event.getTrackId()), event);
    }
}