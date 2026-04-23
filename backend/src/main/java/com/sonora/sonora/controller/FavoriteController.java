package com.sonora.sonora.controller;

import com.sonora.sonora.dto.FavoriteResponse;
import com.sonora.sonora.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST controller for favorite track operations. */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Manage user favorite tracks")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    /** Favorite service. */
    private final FavoriteService favoriteService;

    /**
     * Adds a track to favorites.
     *
     * @param trackId track identifier
     * @param email authenticated user email
     * @return created favorite payload
     */
    @PostMapping("/{trackId}")
    @Operation(summary = "Add track to favorites")
    public ResponseEntity<FavoriteResponse> add(
            @PathVariable final Long trackId,
            @AuthenticationPrincipal final String email) {
        FavoriteResponse response = favoriteService.addFavorite(trackId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Removes a track from favorites.
     *
     * @param trackId track identifier
     * @param email authenticated user email
     */
    @DeleteMapping("/{trackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove track from favorites")
    public void remove(@PathVariable final Long trackId,
                       @AuthenticationPrincipal final String email) {
        favoriteService.removeFavorite(trackId, email);
    }

    /**
     * Lists favorite tracks for the authenticated user.
     *
     * @param email authenticated user email
     * @return favorite tracks
     */
    @GetMapping
    @Operation(summary = "List favorite tracks")
    public List<FavoriteResponse> list(
            @AuthenticationPrincipal final String email) {
        return favoriteService.listFavorites(email);
    }
}
