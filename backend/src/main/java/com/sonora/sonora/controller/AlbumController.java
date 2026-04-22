package com.sonora.sonora.controller;

import com.sonora.sonora.dto.AlbumCreateUpdateDto;
import com.sonora.sonora.dto.AlbumDto;
import com.sonora.sonora.service.AlbumService;
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

/** REST controller for album operations. */
@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@Tag(name = "Albums", description = "Manage music albums")
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    /** Album service. */
    private final AlbumService albumService;

    /**
     * Creates an album.
     *
     * @param dto album payload
     * @return created album
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an album")
    public AlbumDto create(@Valid @RequestBody final AlbumCreateUpdateDto dto) {
        return albumService.create(dto);
    }

    /**
     * Lists all albums.
     *
     * @return list of albums
     */
    @GetMapping
    @Operation(summary = "List all albums")
    public List<AlbumDto> getAll() {
        return albumService.getAll();
    }

    /**
     * Returns one album by identifier.
     *
     * @param id album identifier
     * @return album details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get album by id")
    public AlbumDto getById(@PathVariable final Long id) {
        return albumService.getById(id);
    }

    /**
     * Updates an album.
     *
     * @param id album identifier
     * @param dto update payload
     * @return updated album
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an album")
    public AlbumDto update(@PathVariable final Long id,
                           @Valid @RequestBody final AlbumCreateUpdateDto dto) {
        return albumService.update(id, dto);
    }

    /**
     * Deletes an album.
     *
     * @param id album identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an album")
    public void delete(@PathVariable final Long id) {
        albumService.delete(id);
    }
}
