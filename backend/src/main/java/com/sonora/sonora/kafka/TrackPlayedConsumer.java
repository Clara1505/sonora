package com.sonora.sonora.kafka;

import com.sonora.sonora.model.entity.PlayHistory;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.repository.PlayHistoryRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Consumer persisting played-track events into play history. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedConsumer {

    /** Repository for play history persistence operations. */
    private final PlayHistoryRepository playHistoryRepository;
    /** Repository for track lookups. */
    private final TrackRepository trackRepository;
    /** Repository for user lookups. */
    private final UserRepository userRepository;

    /**
     * Consumes a track played event and stores play history.
     *
     * @param event played track event payload
     */
    @KafkaListener(topics = "track.played", groupId = "sonora-group")
    public void consume(final TrackPlayedEvent event) {
        log.info("Event reçu [track.played] : trackId={}, user={}",
                event.getTrackId(), event.getUserEmail());

        Track track = trackRepository.findById(event.getTrackId()).orElse(null);
        if (track == null) {
            log.warn(
                    "Track introuvable pour l'event track.played : {}",
                    event.getTrackId()
            );
            return;
        }

        User user = userRepository.findByEmail(event.getUserEmail())
                .orElse(null);
        if (user == null) {
            log.warn(
                    "User introuvable pour l'event track.played : {}",
                    event.getUserEmail()
            );
            return;
        }

        PlayHistory history = PlayHistory.builder()
                .track(track)
                .user(user)
                .durationListened(event.getDurationListened())
                .build();

        playHistoryRepository.save(history);
        log.info(
            "PlayHistory sauvegarde : trackId={}, user={}",
            track.getId(),
            user.getEmail()
        );
    }
}
