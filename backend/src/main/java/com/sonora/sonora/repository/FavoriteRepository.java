package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserEmail(String email);
    Optional<Favorite> findByUserEmailAndTrackId(String email, Long trackId);
    boolean existsByUserEmailAndTrackId(String email, Long trackId);
    void deleteByTrackId(Long trackId);
}
