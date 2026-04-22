package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for artist entities. */
public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
