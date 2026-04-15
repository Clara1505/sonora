package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    long countByTrackId(Long trackId);
    List<PlayHistory> findByUserEmail(String email);

    @Query("SELECT SUM(p.durationListened) FROM PlayHistory p WHERE p.track.id = :trackId")
    Long sumDurationByTrackId(@Param("trackId") Long trackId);
}