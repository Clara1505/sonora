package com.sonora.sonora.controller;

import com.sonora.sonora.dto.AlbumCreateUpdateDto;
import com.sonora.sonora.dto.AlbumDto;
import com.sonora.sonora.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDto create(@Valid @RequestBody AlbumCreateUpdateDto dto) {
        return albumService.create(dto);
    }

    @GetMapping
    public List<AlbumDto> getAll() {
        return albumService.getAll();
    }

    @GetMapping("/{id}")
    public AlbumDto getById(@PathVariable Long id) {
        return albumService.getById(id);
    }

    @PutMapping("/{id}")
    public AlbumDto update(@PathVariable Long id, @Valid @RequestBody AlbumCreateUpdateDto dto) {
        return albumService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        albumService.delete(id);
    }
}
