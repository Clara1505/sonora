package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/** Repository for playlist entities. */
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    /**
     * Finds playlists owned by a user.
     *
     * @param email user email
     * @return matching playlists
     */
    List<Playlist> findByUserEmail(String email);

    /**
     * Deletes all references to a track from playlist join table.
     *
     * @param trackId identifier of the track to remove
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM playlist_tracks "
            + "WHERE track_id = :trackId",
            nativeQuery = true)
    void deleteTrackFromPlaylists(Long trackId);
}
