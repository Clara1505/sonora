package com.sonora.sonora.service;

import com.sonora.sonora.dto.FavoriteResponse;
import com.sonora.sonora.exception.AlreadyFavoriteException;
import com.sonora.sonora.exception.ResourceNotFoundException;
import com.sonora.sonora.model.entity.Favorite;
import com.sonora.sonora.model.entity.Track;
import com.sonora.sonora.model.entity.User;
import com.sonora.sonora.repository.FavoriteRepository;
import com.sonora.sonora.repository.TrackRepository;
import com.sonora.sonora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/** Service handling favorite track operations for users. */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    /** Repository for favorites. */
    private final FavoriteRepository favoriteRepository;
    /** Repository for track lookups. */
    private final TrackRepository trackRepository;
    /** Repository for user lookups. */
    private final UserRepository userRepository;

    /**
     * Adds a track to the user's favorites.
     *
     * @param trackId track identifier
     * @param userEmail user email
     * @return created favorite response
     */
    public FavoriteResponse addFavorite(final Long trackId,
                                        final String userEmail) {
        if (favoriteRepository.existsByUserEmailAndTrackId(
                userEmail,
                trackId)) {
            throw new AlreadyFavoriteException(trackId);
        }

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Track not found: " + trackId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userEmail));

        Favorite favorite = Favorite.builder()
                .track(track)
                .user(user)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        log.info("Favori ajouté : user={}, trackId={}", userEmail, trackId);
        return FavoriteResponse.from(saved);
    }

    /**
     * Removes a track from the user's favorites.
     *
     * @param trackId track identifier
     * @param userEmail user email
     */
    public void removeFavorite(final Long trackId, final String userEmail) {
        Favorite favorite = favoriteRepository.findByUserEmailAndTrackId(
                        userEmail,
                        trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite not found for track: " + trackId));

        favoriteRepository.delete(favorite);
        log.info("Favori supprimé : user={}, trackId={}", userEmail, trackId);
    }

    /**
     * Lists all favorite tracks of a user.
     *
     * @param userEmail user email
     * @return list of favorite responses
     */
    public List<FavoriteResponse> listFavorites(final String userEmail) {
        return favoriteRepository.findByUserEmail(userEmail)
                .stream()
                .map(FavoriteResponse::from)
                .toList();
    }
}
