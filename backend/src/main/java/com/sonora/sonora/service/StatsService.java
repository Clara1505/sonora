package com.sonora.sonora.service;

import com.sonora.sonora.dto.TrackStatsResponse;
import com.sonora.sonora.dto.UserStatsResponse;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.PlayHistory;
import com.sonora.sonora.repository.PlayHistoryRepository;
import com.sonora.sonora.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Service providing listening statistics aggregations. */
@Service
@RequiredArgsConstructor
public class StatsService {

        /** Default number of top tracks to return. */
        private static final int TOP_TRACK_LIMIT = 10;

        /** Repository for play history queries. */
    private final PlayHistoryRepository playHistoryRepository;
        /** Repository for track lookups. */
        private final TrackRepository trackRepository;

        /**
         * Builds aggregate statistics for a track.
         *
         * @param trackId track identifier
         * @return track statistics response
         */
        public TrackStatsResponse getTrackStats(final Long trackId) {
        var track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Track not found: " + trackId));

        long playCount = playHistoryRepository.countByTrackId(trackId);
        Long totalDuration =
                playHistoryRepository.sumDurationByTrackId(trackId);

        return TrackStatsResponse.builder()
                .trackId(trackId)
                .trackTitle(track.getTitle())
                .playCount(playCount)
                .totalDurationListened(
                        totalDuration != null ? totalDuration : 0L)
                .build();
    }

        /**
         * Builds aggregate listening statistics for a user.
         *
         * @param userEmail user email
         * @return user statistics response
         */
        public UserStatsResponse getUserStats(final String userEmail) {
        List<PlayHistory> history =
                playHistoryRepository.findByUserEmail(userEmail);

        Map<Long, List<PlayHistory>> byTrack = history.stream()
                .collect(Collectors.groupingBy(h -> h.getTrack().getId()));

        List<TrackStatsResponse> topTracks = byTrack.entrySet().stream()
                .map(entry -> TrackStatsResponse.builder()
                        .trackId(entry.getKey())
                        .trackTitle(
                                entry.getValue().get(0)
                                        .getTrack()
                                        .getTitle())
                        .playCount(entry.getValue().size())
                        .totalDurationListened(entry.getValue().stream()
                                .mapToLong(h -> h.getDurationListened() != null
                                        ? h.getDurationListened() : 0)
                                .sum())
                        .build())
                .sorted((a, b) -> Long.compare(
                        b.getPlayCount(),
                        a.getPlayCount()))
                .limit(TOP_TRACK_LIMIT)
                .toList();

        return UserStatsResponse.builder()
                .userEmail(userEmail)
                .totalPlays(history.size())
                .topTracks(topTracks)
                .build();
    }
}
