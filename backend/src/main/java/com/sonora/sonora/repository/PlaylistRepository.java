package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM playlist_tracks WHERE track_id = :trackId", nativeQuery = true)
    void deleteTrackFromPlaylists(Long trackId);
}
