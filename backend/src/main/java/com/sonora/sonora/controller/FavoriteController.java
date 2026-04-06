package com.sonora.sonora.controller;

import com.sonora.sonora.dto.FavoriteResponse;
import com.sonora.sonora.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{trackId}")
    public ResponseEntity<FavoriteResponse> add(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails userDetails) {
        FavoriteResponse response = favoriteService.addFavorite(trackId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{trackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.removeFavorite(trackId, userDetails.getUsername());
    }

    @GetMapping
    public List<FavoriteResponse> list(
            @AuthenticationPrincipal UserDetails userDetails) {
        return favoriteService.listFavorites(userDetails.getUsername());
    }
}