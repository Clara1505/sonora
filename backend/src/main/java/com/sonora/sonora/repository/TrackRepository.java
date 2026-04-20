package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByMoodAndGenre(Mood mood, Genre genre);
    List<Track> findByGenre(Genre genre);
    List<Track> findByMood(Mood mood);
}
