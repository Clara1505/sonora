package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repository for track entities and recommendation queries. */
public interface TrackRepository extends JpaRepository<Track, Long> {
    /**
     * Finds tracks matching both mood and genre.
     *
     * @param mood mood filter
     * @param genre genre filter
     * @return matching tracks
     */
    List<Track> findByMoodAndGenre(Mood mood, Genre genre);

    /**
     * Finds tracks by genre.
     *
     * @param genre genre filter
     * @return matching tracks
     */
    List<Track> findByGenre(Genre genre);

    /**
     * Finds tracks by mood.
     *
     * @param mood mood filter
     * @return matching tracks
     */
    List<Track> findByMood(Mood mood);
}
