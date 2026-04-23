package com.sonora.sonora.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Consumer logging played-track events
 * (persistence is handled synchronously
 * by the REST endpoint). */
@Slf4j
@Component
public class TrackPlayedConsumer {

    /**
     * Consumes a track played event for analytics/logging purposes.
     *
     * @param event played track event payload
     */
    @KafkaListener(topics = "track.played", groupId = "sonora-group")
    public void consume(final TrackPlayedEvent event) {
        log.info("Event recu [track.played] :"
                + " trackId={}, user={}, duration={}s",
                event.getTrackId(),
                event.getUserEmail(),
                event.getDurationListened());
    }
}
