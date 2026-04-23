package com.sonora.sonora.repository;

import com.sonora.sonora.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for favorite entities. */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    /**
     * Finds favorites by user email.
     *
     * @param email user email
     * @return matching favorites
     */
    List<Favorite> findByUserEmail(String email);

    /**
     * Finds one favorite by user email and track identifier.
     *
     * @param email user email
     * @param trackId track identifier
     * @return optional favorite
     */
    Optional<Favorite> findByUserEmailAndTrackId(String email,
                                                 Long trackId);

    /**
     * Checks whether a favorite already exists.
     *
     * @param email user email
     * @param trackId track identifier
     * @return true when favorite exists
     */
    boolean existsByUserEmailAndTrackId(String email,
                                        Long trackId);
    /**
     * Deletes favorites referring to the given track identifier.
     *
     * @param trackId track identifier to remove from favorites
     */
    void deleteByTrackId(Long trackId);
}
