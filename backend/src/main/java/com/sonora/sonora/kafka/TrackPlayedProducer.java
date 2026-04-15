package com.sonora.sonora.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedProducer {

    static final String TOPIC_TRACK_PLAYED = "track.played";

    private final KafkaTemplate<String, TrackPlayedEvent> kafkaTemplate;

    public void sendTrackPlayed(TrackPlayedEvent event) {
        log.info("Publication Kafka [{}] : trackId={}, user={}",
                TOPIC_TRACK_PLAYED, event.getTrackId(), event.getUserEmail());
        kafkaTemplate.send(TOPIC_TRACK_PLAYED, String.valueOf(event.getTrackId()), event);
    }
}
