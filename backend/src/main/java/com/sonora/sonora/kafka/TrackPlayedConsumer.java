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

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackPlayedConsumer {

    private final PlayHistoryRepository playHistoryRepository;
    private final TrackRepository       trackRepository;
    private final UserRepository        userRepository;

    @KafkaListener(topics = "track.played", groupId = "sonora-group")
    public void consume(TrackPlayedEvent event) {
        log.info("Event reçu [track.played] : trackId={}, user={}",
                event.getTrackId(), event.getUserEmail());

        Track track = trackRepository.findById(event.getTrackId()).orElse(null);
        if (track == null) {
            log.warn("Track introuvable pour l'event track.played : {}", event.getTrackId());
            return;
        }

        User user = userRepository.findByEmail(event.getUserEmail()).orElse(null);
        if (user == null) {
            log.warn("User introuvable pour l'event track.played : {}", event.getUserEmail());
            return;
        }

        PlayHistory history = PlayHistory.builder()
                .track(track)
                .user(user)
                .durationListened(event.getDurationListened())
                .build();

        playHistoryRepository.save(history);
        log.info("PlayHistory sauvegardé : trackId={}, user={}", track.getId(), user.getEmail());
    }
}