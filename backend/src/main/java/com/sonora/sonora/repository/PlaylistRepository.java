package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repository for playlist entities. */
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    /**
     * Finds playlists owned by a user.
     *
     * @param email user email
     * @return matching playlists
     */
    List<Playlist> findByUserEmail(String email);
}
