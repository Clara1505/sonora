package com.sonora.sonora.controller;

import com.sonora.sonora.dto.ArtistCreateUpdateDto;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

/** REST controller for artist operations. */
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Manage artists")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    /** Artist service. */
    private final ArtistService artistService;

    /**
     * Creates an artist.
     *
     * @param dto artist payload
     * @return created artist
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an artist")
    public ArtistDto create(
            @Valid @RequestBody final ArtistCreateUpdateDto dto) {
        return artistService.create(dto);
    }

    /**
     * Lists all artists.
     *
     * @return list of artists
     */
    @GetMapping
    @Operation(summary = "List all artists")
    public List<ArtistDto> getAll() {
        return artistService.getAll();
    }

    /**
     * Returns one artist by identifier.
     *
     * @param id artist identifier
     * @return artist details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get artist by id")
    public ArtistDto getById(@PathVariable final Long id) {
        return artistService.getById(id);
    }

    /**
     * Updates an artist.
     *
     * @param id artist identifier
     * @param dto update payload
     * @return updated artist
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an artist")
    public ArtistDto update(
            @PathVariable final Long id,
            @Valid @RequestBody final ArtistCreateUpdateDto dto) {
        return artistService.update(id, dto);
    }

    /**
     * Deletes an artist.
     *
     * @param id artist identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an artist")
    public void delete(@PathVariable final Long id) {
        artistService.delete(id);
    }
}
