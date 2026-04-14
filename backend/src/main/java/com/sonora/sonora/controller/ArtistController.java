package com.sonora.sonora.controller;

import com.sonora.sonora.dto.ArtistCreateUpdateDto;
import com.sonora.sonora.dto.ArtistDto;
import com.sonora.sonora.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistDto create(@Valid @RequestBody ArtistCreateUpdateDto dto) {
        return artistService.create(dto);
    }

    @GetMapping
    public List<ArtistDto> getAll() {
        return artistService.getAll();
    }

    @GetMapping("/{id}")
    public ArtistDto getById(@PathVariable Long id) {
        return artistService.getById(id);
    }

    @PutMapping("/{id}")
    public ArtistDto update(@PathVariable Long id, @Valid @RequestBody ArtistCreateUpdateDto dto) {
        return artistService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        artistService.delete(id);
    }
}
