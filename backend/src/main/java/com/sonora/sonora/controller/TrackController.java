package com.sonora.sonora.controller;

import com.sonora.sonora.dto.TrackCreateUpdateDto;
import com.sonora.sonora.dto.TrackDto;
import com.sonora.sonora.service.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST controller for track CRUD operations. */
@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
@Tag(name = "Tracks", description = "Manage tracks")
@SecurityRequirement(name = "bearerAuth")
public class TrackController {

    /** Track service. */
    private final TrackService trackService;

    /**
     * Creates a track.
     *
     * @param dto track payload
     * @return created track
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a track")
    public TrackDto create(
            @Valid @RequestBody final TrackCreateUpdateDto dto,
            @AuthenticationPrincipal String email) {
        return trackService.create(dto, email);
    }

    /**
     * Lists all tracks.
     *
     * @return track list
     */
    @GetMapping
    @Operation(summary = "List all tracks")
    public List<TrackDto> getAll() {
        return trackService.getAll();
    }

    /**
     * Returns a track by identifier.
     *
     * @param id track identifier
     * @return track details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get track by id")
    public TrackDto getById(@PathVariable final Long id) {
        return trackService.getById(id);
    }

    /**
     * Updates a track.
     *
     * @param id track identifier
     * @param dto update payload
     * @return updated track
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a track")
    @PreAuthorize("hasRole('ADMIN')")
    public TrackDto update(@PathVariable final Long id,
                           @Valid @RequestBody final TrackCreateUpdateDto dto) {
        return trackService.update(id, dto);
    }

    /**
     * Deletes a track.
     *
     * @param id track identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a track")
    public void delete(
            @PathVariable final Long id,
            @AuthenticationPrincipal String email,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        trackService.delete(id, email, isAdmin);
    }
}
