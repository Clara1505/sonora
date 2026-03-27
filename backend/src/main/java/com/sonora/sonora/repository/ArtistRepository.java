package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
