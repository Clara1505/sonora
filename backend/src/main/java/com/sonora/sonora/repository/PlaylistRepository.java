package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserEmail(String email);
}
