package com.sonora.sonora.controller;

import com.sonora.sonora.dto.PlaylistCreateDto;
import com.sonora.sonora.dto.PlaylistDto;
import com.sonora.sonora.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistDto> create(
            @RequestBody PlaylistCreateDto dto,
            @AuthenticationPrincipal String email) {
        PlaylistDto playlist = playlistService.create(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

    @GetMapping
    public List<PlaylistDto> list(@AuthenticationPrincipal String email) {
        return playlistService.getUserPlaylists(email);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PlaylistDto> listAll() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{id}")
    public PlaylistDto getById(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return playlistService.getById(id, email);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String email,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            playlistService.adminDelete(id);
        } else {
            playlistService.delete(id, email);
        }
    }

    @PostMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<PlaylistDto> addTrack(
            @PathVariable Long id,
            @PathVariable Long trackId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(playlistService.addTrack(id, trackId, email));
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<PlaylistDto> removeTrack(
            @PathVariable Long id,
            @PathVariable Long trackId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(playlistService.removeTrack(id, trackId, email));
    }
}
