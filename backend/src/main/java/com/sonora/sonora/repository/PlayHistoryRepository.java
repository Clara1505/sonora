package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository for play history entities and aggregates. */
@Repository
public interface PlayHistoryRepository
    extends JpaRepository<PlayHistory, Long> {
    /**
     * Counts play history rows for a track.
     *
     * @param trackId track identifier
     * @return play count
     */
    long countByTrackId(Long trackId);

    /**
     * Finds play history rows by user email.
     *
     * @param email user email
     * @return matching play history rows
     */
    List<PlayHistory> findByUserEmail(String email);

    /**
     * Sums listened duration for a track.
     *
     * @param trackId track identifier
     * @return sum of listened duration
     */
    @Query("SELECT SUM(p.durationListened) FROM PlayHistory p "
            + "WHERE p.track.id = :trackId")
    Long sumDurationByTrackId(@Param("trackId") Long trackId);
}
