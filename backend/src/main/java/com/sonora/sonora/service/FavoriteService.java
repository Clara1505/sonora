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

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final TrackRepository    trackRepository;
    private final UserRepository     userRepository;

    public FavoriteResponse addFavorite(Long trackId, String userEmail) {
        if (favoriteRepository.existsByUserEmailAndTrackId(userEmail, trackId)) {
            throw new AlreadyFavoriteException(trackId);
        }

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found: " + trackId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Favorite favorite = Favorite.builder()
                .track(track)
                .user(user)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        log.info("Favori ajouté : user={}, trackId={}", userEmail, trackId);
        return FavoriteResponse.from(saved);
    }

    public void removeFavorite(Long trackId, String userEmail) {
        Favorite favorite = favoriteRepository.findByUserEmailAndTrackId(userEmail, trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite not found for track: " + trackId));

        favoriteRepository.delete(favorite);
        log.info("Favori supprimé : user={}, trackId={}", userEmail, trackId);
    }

    public List<FavoriteResponse> listFavorites(String userEmail) {
        return favoriteRepository.findByUserEmail(userEmail)
                .stream()
                .map(FavoriteResponse::from)
                .toList();
    }
}