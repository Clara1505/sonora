package com.sonora.sonora.controller;

import com.sonora.sonora.dto.PlaylistCreateDto;
import com.sonora.sonora.dto.PlaylistDto;
import com.sonora.sonora.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST controller for playlist operations. */
@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
@Tag(name = "Playlists", description = "Manage user playlists")
@SecurityRequirement(name = "bearerAuth")
public class PlaylistController {

    /** Playlist service. */
    private final PlaylistService playlistService;

    /**
     * Creates a playlist.
     *
     * @param dto creation payload
     * @param email authenticated user email
     * @return created playlist
     */
    @PostMapping
    @Operation(summary = "Create a playlist")
    public ResponseEntity<PlaylistDto> create(
            @RequestBody final PlaylistCreateDto dto,
            @AuthenticationPrincipal final String email) {
        PlaylistDto playlist = playlistService.create(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

    /**
     * Lists playlists for the authenticated user.
     *
     * @param email authenticated user email
     * @return list of playlists
     */
    @GetMapping
    @Operation(summary = "List user playlists")
    public List<PlaylistDto> list(@AuthenticationPrincipal final String email) {
        return playlistService.getUserPlaylists(email);
    }

    /**
     * Lists all playlists (admin only).
     *
     * @return all playlists
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PlaylistDto> listAll() {
        return playlistService.getAllPlaylists();
    }

    /**
     * Returns one playlist by identifier.
     *
     * @param id playlist identifier
     * @param email authenticated user email
     * @return playlist details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get playlist by id")
    public PlaylistDto getById(@PathVariable final Long id,
                               @AuthenticationPrincipal final String email) {
        return playlistService.getById(id, email);
    }

    /**
     * Deletes a playlist. If the authenticated user is an admin the playlist
     * is removed globally, otherwise only the owner's playlist is deleted.
     *
     * @param id playlist identifier
     * @param email authenticated user email
     * @param authentication authentication token (may be null)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a playlist")
    public void delete(
            @PathVariable final Long id,
            @AuthenticationPrincipal final String email,
            final Authentication authentication) {
        boolean isAdmin = false;
        if (authentication != null) {
            isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        if (isAdmin) {
            playlistService.adminDelete(id);
        } else {
            playlistService.delete(id, email);
        }
    }

    /**
     * Adds a track to a playlist.
     *
     * @param id playlist identifier
     * @param trackId track identifier
     * @param email authenticated user email
     * @return updated playlist
     */
    @PostMapping("/{id}/tracks/{trackId}")
    @Operation(summary = "Add track to playlist")
    public ResponseEntity<PlaylistDto> addTrack(
            @PathVariable final Long id,
            @PathVariable final Long trackId,
            @AuthenticationPrincipal final String email) {
        return ResponseEntity.ok(playlistService.addTrack(id, trackId, email));
    }

    /**
     * Removes a track from a playlist.
     *
     * @param id playlist identifier
     * @param trackId track identifier
     * @param email authenticated user email
     * @return updated playlist
     */
    @DeleteMapping("/{id}/tracks/{trackId}")
    @Operation(summary = "Remove track from playlist")
    public ResponseEntity<PlaylistDto> removeTrack(
            @PathVariable final Long id,
            @PathVariable final Long trackId,
            @AuthenticationPrincipal final String email) {
        return ResponseEntity.ok(
                playlistService.removeTrack(id, trackId, email)
        );
    }
}
